package com.mung.api.controller.order;

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
import com.mung.order.dto.OrderDto.OrderCancelRequest;
import com.mung.order.dto.OrderDto.OrderSearchRequest;
import com.mung.order.repository.OrderRepository;
import com.mung.payment.repository.KakaopayLogRepository;
import com.mung.payment.repository.PaymentRepository;
import com.mung.payment.service.KakaopayService;
import com.mung.payment.service.PaymentService;
import com.mung.stock.domain.Stock;
import com.mung.stock.repository.StockRepository;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import org.junit.jupiter.api.Disabled;
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
import org.springframework.test.annotation.Rollback;
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
    @Mock
    KakaopayLogRepository kakaoPayLogRepository;
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
    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private KakaopayService kakaopayService;

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
    @Disabled
    @Rollback(value = false)
    @MockMember(id = 1L, name = "USER", role = Role.USER)
    public void 주문취소_성공() throws Exception {
        // given
        OrderCancelRequest request = OrderCancelRequest.builder()
            .orderId(353L)
            .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(patch("/order")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
            .andExpect(jsonPath("$.data.tid").isNotEmpty())
            .andDo(print());
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