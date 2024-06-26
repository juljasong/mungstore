package com.mung.api.controller.payment;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mung.api.controller.MockMember;
import com.mung.common.domain.Validate.Message;
import com.mung.member.domain.Role;
import com.mung.order.dto.OrderDto.OrderItemDto;
import com.mung.order.dto.OrderDto.OrderRequest;
import com.mung.payment.dto.PaymentDto.CancelPaymentRequest;
import com.mung.stock.repository.StockRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class PaymentControllerTest {

    @Autowired
    PaymentController paymentController;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private StockRepository stockRepository;

    @Test
    //@Rollback(value = false)
    @MockMember(id = 1L, name = "USER", role = Role.USER)
    public void 카카오결제준비_성공() throws Exception {
        // given
        String agent = "pc";
        List<OrderItemDto> orders = new ArrayList<>();
        orders.add(OrderItemDto.builder()
            .productId(1L)
            .productName("pname1")
            .optionId(1L)
            .quantity(2)
            .orderPrice(400)
            .build());
        orders.add(OrderItemDto.builder()
            .productId(1L)
            .productName("pname2")
            .optionId(2L)
            .quantity(1)
            .orderPrice(410)
            .contents("메모")
            .build());

        OrderRequest orderReq = OrderRequest.builder()
            .orderItems(orders)
            .totalPrice(1210)
            .tel1("01011111111")
            .tel2("01011112222")
            .zipcode("12345")
            .city("시티")
            .street("스트릿")
            .build();

        String json = objectMapper.writeValueAsString(orderReq);

        // expected
        mockMvc.perform(post("/payment/kakaopay/ready/" + agent)
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
            .andDo(print());
    }

    @Test
    @Disabled
    @Rollback(value = false)
    @MockMember(id = 1L, name = "USER", role = Role.USER)
    public void 카카오결제승인_성공() throws Exception {
        // given
        String agent = "pc";

        // expected
        mockMvc.perform(
                get("/payment/kakaopay/approve/" + agent + "?id=357&pg_token=1e38bd1ea50033af9b8f")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
            .andDo(print());
    }

    @Test
    @Disabled
    @Rollback(value = false)
    @MockMember(id = 1L, name = "USER", role = Role.USER)
    public void 카카오결제승인_실패_재고초과() throws Exception {
        // given
        String agent = "pc";

        // expected
        mockMvc.perform(
                get("/payment/kakaopay/approve/" + agent + "?id=354&pg_token=4a596ecd8d822753031c")
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.data.message").value(Message.OUT_OF_STOCK))
            .andDo(print());
    }

    @Test
    @Disabled
    @Rollback(value = false)
    @MockMember(id = 1L, name = "USER", role = Role.USER)
    public void 카카오결제취소_성공() throws Exception {
        // given
        String agent = "pc";
        CancelPaymentRequest orderReq = CancelPaymentRequest.builder()
            .orderId(337L)
            .build();

        String json = objectMapper.writeValueAsString(orderReq);

        // expected
        mockMvc.perform(post("/payment/kakaopay/cancel")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
            .andExpect(jsonPath("$.data.tid").isNotEmpty())
            .andDo(print());
    }

}