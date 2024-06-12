package com.mung.api.controller.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mung.api.controller.MockMember;
import com.mung.common.domain.Address;
import com.mung.common.domain.Validate.Message;
import com.mung.member.config.JwtUtil;
import com.mung.member.domain.Role;
import com.mung.order.dto.OrderDto.Order;
import com.mung.order.dto.OrderDto.OrderRequest;
import com.mung.order.repository.OrderRepository;
import com.mung.stock.repository.StockRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class OrderControllerTest {

    @MockBean
    JwtUtil jwtUtil;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private StockRepository stockRepository;

    @Test
    @MockMember(id = 1L, name = "USER", role = Role.USER)
    public void 주문_성공() throws Exception {
        // given
        given(jwtUtil.getMemberId(anyString()))
            .willReturn(1L);

        int beforeStock1 = stockRepository.findByOptionId(1L).get().getQuantity();
        int beforeStock2 = stockRepository.findByOptionId(2L).get().getQuantity();

        List<Order> orders = new ArrayList<>();
        orders.add(Order.builder()
            .productId(1L)
            .productName("pname1")
            .optionId(1L)
            .quantity(2)
            .orderPrice(1200)
            .build());
        orders.add(Order.builder()
            .productId(1L)
            .productName("pname2")
            .optionId(2L)
            .quantity(1)
            .orderPrice(1500)
            .contents("메모")
            .build());

        OrderRequest orderReq = OrderRequest.builder()
            .orders(orders)
            .totalPrice(2700)
            .tel1("01011111111")
            .tel2("01011112222")
            .address(new Address("12345", "시티", "스트릿"))
            .build();

        String json = objectMapper.writeValueAsString(orderReq);

        // expected
        mockMvc.perform(post("/order")
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer test")
                .content(json)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
            .andExpect(jsonPath("$.data.orderId").exists())
            .andDo(print());

        // then
        int stock1 = stockRepository.findByOptionId(1L).get().getQuantity();
        int stock2 = stockRepository.findByOptionId(2L).get().getQuantity();
        assertEquals(2, beforeStock1 - stock1);
        assertEquals(1, beforeStock2 - stock2);
    }

    @Test
    @MockMember(id = 1L, name = "USER", role = Role.USER)
    public void 주문_실패_재고초과() throws Exception {
        // given
        given(jwtUtil.getMemberId(anyString()))
            .willReturn(1L);

        List<Order> orders = new ArrayList<>();
        orders.add(Order.builder()
            .productId(1L)
            .productName("pname1")
            .optionId(1L)
            .quantity(2102340)
            .orderPrice(1200)
            .build());

        OrderRequest orderReq = OrderRequest.builder()
            .orders(orders)
            .totalPrice(2700)
            .tel1("01011111111")
            .tel2("01011112222")
            .address(new Address("12345", "시티", "스트릿"))
            .build();

        String json = objectMapper.writeValueAsString(orderReq);

        // expected
        mockMvc.perform(post("/order")
                .contentType(APPLICATION_JSON)
                .header("Authorization", "Bearer test")
                .content(json)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(Message.OUT_OF_STOCK))
            .andDo(print());

    }


}