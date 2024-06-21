package com.mung.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.mung.order.domain.Orders;
import com.mung.order.dto.OrderDto.OrderRequest;
import com.mung.order.service.OrderService;
import com.mung.payment.domain.KakaopayPayment;
import com.mung.payment.dto.KakaopayDto.KakaopayApproveResponse;
import com.mung.payment.dto.KakaopayDto.KakaopayApproveResponse.Amount;
import com.mung.payment.dto.KakaopayDto.KakaopayReadyResponse;
import com.mung.payment.dto.PaymentDto.CompletePaymentResponse;
import com.mung.payment.repository.KakaopayPaymentRedisRepository;
import com.mung.payment.repository.PaymentRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class PaymentServiceTest {

    @InjectMocks
    PaymentService paymentService;

    @Mock
    PaymentRepository paymentRepository;
    @Mock
    KakaopayPaymentRedisRepository kakaopayPaymentRedisRepository;

    @Mock
    OrderService orderService;
    @Mock
    KakaopayService kakaopayService;

    @Test
    public void 카카오결제요청_성공() {
        // given
        Orders order = Orders.builder().build();
        ReflectionTestUtils.setField(order, "id", 1L);
        given(orderService.requestOrder(any(), anyLong()))
            .willReturn(order);

        given(kakaopayService.ready(anyString(), any(), any(), anyLong()))
            .willReturn(KakaopayReadyResponse.builder()
                .tid("tid")
                .build());

        // when
        KakaopayReadyResponse response = paymentService.readyKakaoPayment("agent",
            OrderRequest.builder().build(), 1L);

        // then
        assertEquals("tid", response.getTid());
        verify(kakaopayPaymentRedisRepository).save(any());
    }

    @Test
    public void 카카오결제승인_성공() {
        // given
        given(kakaopayPaymentRedisRepository.findById(anyLong()))
            .willReturn(Optional.of(KakaopayPayment.builder()
                .orderId(1L)
                .memberId(1L)
                .tid("tid")
                .build()));
        given(kakaopayService.approve(anyString(), any()))
            .willReturn(KakaopayApproveResponse.builder()
                .partnerOrderId("1")
                .paymentMethodType("MONEY")
                .amount(Amount.builder().total(1000).build())
                .build());
        given(orderService.getOrder(anyLong(), anyLong()))
            .willReturn(Orders.builder()
                .totalPrice(1000)
                .build());

        Orders order = Orders.builder().build();
        ReflectionTestUtils.setField(order, "id", 1L);
        given(orderService.updateOrderStatus(anyLong(), any()))
            .willReturn(order);

        // when
        CompletePaymentResponse response = paymentService.completeKakaoPayment("agent", "pgToken",
            1L, 1L);

        // then
        assertEquals(1L, response.getOrderId());
        assertEquals(1000, response.getTotalAmount());
        verify(paymentRepository).save(any());
        verify(kakaopayPaymentRedisRepository).delete(any());
    }

    @Test
    public void 카카오결제승인_실패_금액불일치() {
        // given
        given(kakaopayPaymentRedisRepository.findById(anyLong()))
            .willReturn(Optional.of(KakaopayPayment.builder()
                .orderId(1L)
                .memberId(1L)
                .tid("tid")
                .build()));
        given(kakaopayService.approve(anyString(), any()))
            .willReturn(KakaopayApproveResponse.builder()
                .partnerOrderId("1")
                .paymentMethodType("MONEY")
                .amount(Amount.builder().total(100).build())
                .build());
        given(orderService.getOrder(anyLong(), anyLong()))
            .willReturn(Orders.builder()
                .totalPrice(1000)
                .build());

        // expected
        CompletePaymentResponse response = paymentService.completeKakaoPayment("agent", "pgToken",
            1L, 1L);

        // then
        assertEquals("결제 도중 에러가 발생했습니다.", response.getMessage());
        verify(kakaopayService).cancel(any());
    }

    @Test
    public void 카카오결제승인_실패_승인실패() {
        // given
        given(kakaopayPaymentRedisRepository.findById(anyLong()))
            .willReturn(Optional.of(KakaopayPayment.builder()
                .orderId(1L)
                .memberId(1L)
                .tid("tid")
                .build()));
        given(kakaopayService.approve(anyString(), any()))
            .willThrow(RuntimeException.class);

        // expected
        CompletePaymentResponse response = paymentService.completeKakaoPayment("agent", "pgToken",
            1L, 1L);

        // then
        assertEquals("결제 도중 에러가 발생했습니다.", response.getMessage());
    }

}