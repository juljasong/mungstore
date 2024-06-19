package com.mung.api.controller.order;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mung.api.controller.MockMember;
import com.mung.common.domain.Validate.Message;
import com.mung.common.exception.BadRequestException;
import com.mung.member.config.JwtUtil;
import com.mung.member.domain.Role;
import com.mung.order.domain.Orders;
import com.mung.order.dto.OrderDto.OrderCancelRequest;
import com.mung.order.dto.OrderDto.OrderItemDto;
import com.mung.order.dto.OrderDto.OrderRequest;
import com.mung.order.dto.OrderDto.OrderSearchRequest;
import com.mung.order.repository.OrderRepository;
import com.mung.payment.repository.PaymentRepository;
import com.mung.payment.service.PaymentService;
import com.mung.stock.domain.Stock;
import com.mung.stock.repository.StockRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class OrderControllerTest {

    private static final Logger log = LoggerFactory.getLogger(OrderControllerTest.class);
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
    @Autowired
    private PaymentService paymentService;

    @Mock
    private PaymentRepository paymentRepository;

    @Test
    @MockMember(id = 1L, name = "USER", role = Role.USER)
    public void 재고_동시성테스트() throws Exception {

        int numberOfThreads = 100;
        ExecutorService executorService = Executors.newFixedThreadPool(numberOfThreads);
        CountDownLatch latch = new CountDownLatch(numberOfThreads);

        for (int i = 0; i < numberOfThreads; i++) {
            executorService.submit(() -> {
                try {
                    Stock stock = stockRepository.findByOptionId(1L)
                        .orElseThrow(
                            BadRequestException::new);
                    stock.removeStock(1);
                    stockRepository.save(stock);
                } catch (ObjectOptimisticLockingFailureException e) {
                    log.error("exception:: 충돌 감지");
                } catch (Exception e) {
                    log.error("exception:: ", e);
                } finally {
                    latch.countDown();
                }
            });
        }
        latch.await();
    }

    @Test
    @MockMember(id = 1L, name = "USER", role = Role.USER)
    public void 주문_성공() throws Exception {
        // given
        int beforeStock1 = stockRepository.findByOptionId(1L).get().getQuantity();
        int beforeStock2 = stockRepository.findByOptionId(2L).get().getQuantity();

        List<OrderItemDto> orders = new ArrayList<>();
        orders.add(OrderItemDto.builder()
            .productId(1L)
            .productName("pname1")
            .optionId(1L)
            .quantity(2)
            .orderPrice(1200)
            .build());
        orders.add(OrderItemDto.builder()
            .productId(1L)
            .productName("pname2")
            .optionId(2L)
            .quantity(1)
            .orderPrice(1500)
            .contents("메모")
            .build());

        OrderRequest orderReq = OrderRequest.builder()
            .orderItems(orders)
            .totalPrice(2700)
            .tel1("01011111111")
            .tel2("01011112222")
            .zipcode("12345")
            .city("시티")
            .street("스트릿")
            .build();

        String json = objectMapper.writeValueAsString(orderReq);

        // expected
        mockMvc.perform(post("/order")
                .contentType(APPLICATION_JSON)
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
        List<OrderItemDto> orders = new ArrayList<>();
        orders.add(OrderItemDto.builder()
            .productId(1L)
            .productName("pname1")
            .optionId(1L)
            .quantity(2102340)
            .orderPrice(1200)
            .build());

        OrderRequest orderReq = OrderRequest.builder()
            .orderItems(orders)
            .totalPrice(2700)
            .tel1("01011111111")
            .tel2("01011112222")
            .zipcode("12345")
            .city("시티")
            .street("스트릿")
            .build();

        String json = objectMapper.writeValueAsString(orderReq);

        // expected
        mockMvc.perform(post("/order")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(Message.OUT_OF_STOCK))
            .andDo(print());
    }

    @Test
    @MockMember(id = 1L, name = "USER", role = Role.USER)
    public void 주문취소_성공() throws Exception {
        // given
        Orders order = orderRepository.findAll().get(0);
        OrderCancelRequest request = OrderCancelRequest.builder()
            .orderId(order.getId())
            .build();

        String json = objectMapper.writeValueAsString(request);
        int beforeStock1 = order.getOrderItems().get(0).getStock().getQuantity();

        // expected
        mockMvc.perform(patch("/order")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
            .andExpect(jsonPath("$.data.tid").isNotEmpty())
            .andDo(print());

        // then
        int stock1 = order.getOrderItems().get(0).getStock().getQuantity();
        assertEquals(order.getOrderItems().get(0).getQuantity(), (stock1 - beforeStock1));
    }

    @Test
    @MockMember(id = 1L, name = "USER", role = Role.USER)
    public void 주문취소_실패_없는주문() throws Exception {
        // given
        OrderCancelRequest request = OrderCancelRequest.builder()
            .orderId(98475L)
            .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(patch("/order")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(Message.BAD_REQUEST))
            .andDo(print());
    }

    @Test
    @MockMember(id = 1L, name = "USER", role = Role.USER)
    public void 주문조회_단건_성공() throws Exception {
        // given
        Long orderId = orderRepository.findAll().get(0).getId();

        // expected
        mockMvc.perform(get("/order?orderId=" + orderId)
                .contentType(APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
            .andExpect(jsonPath("$.data.orderId").value(orderId))
            .andDo(print());
    }

    @Test
    @MockMember(id = 1L, name = "USER", role = Role.USER)
    public void 주문조회_리스트_성공() throws Exception {
        // given
        OrderSearchRequest request = OrderSearchRequest.builder()
            .memberId(1L)
            .build();
        ReflectionTestUtils.setField(request, "pageNumber", 0);
        ReflectionTestUtils.setField(request, "pageSize", 3);
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/orders")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
            .andExpect(jsonPath("$.data.content").exists())
            .andDo(print());
    }
}