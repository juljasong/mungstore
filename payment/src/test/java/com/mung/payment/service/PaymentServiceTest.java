package com.mung.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.mung.common.domain.Validate.Message;
import com.mung.order.domain.Orders;
import com.mung.order.repository.OrderRepository;
import com.mung.payment.domain.Payment;
import com.mung.payment.dto.PaymentDto.CompletePaymentDto;
import com.mung.payment.dto.PaymentDto.KaKaoCompletePaymentRequest;
import com.mung.payment.dto.PaymentDto.KaKaoCompletePaymentRequest.Amount;
import com.mung.payment.dto.PaymentDto.KaKaoCompletePaymentRequest.CardInfo;
import com.mung.payment.repository.PaymentKaKaoLogRepository;
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
    PaymentKaKaoLogRepository paymentKaKaoLogRepository;
    @Mock
    OrderRepository orderRepository;

    @Test
    public void 카카오결제_성공() {
        // given
        KaKaoCompletePaymentRequest request = KaKaoCompletePaymentRequest.builder()
            .aid("123")
            .partnerUserId("1")
            .partnerOrderId("1")
            .paymentMethodType("CARD")
            .amount(Amount.builder().total(1000).build())
            .cardInfo(CardInfo.builder().build())
            .build();
        Long memberId = 1L;

        Payment payment = new Payment(request);
        ReflectionTestUtils.setField(payment, "id", 1L);
        given(paymentRepository.save(any()))
            .willReturn(payment);

        given(orderRepository.findById(any()))
            .willReturn(Optional.of(Orders.builder().totalPrice(1000).build()));

        // when
        CompletePaymentDto completePaymentDto = paymentService.kakaoComplete(request, memberId);

        // then
        assertEquals(1L, completePaymentDto.getOrderId());
        assertEquals(1000, completePaymentDto.getTotalAmount());
        assertNull(completePaymentDto.getMessage());
        verify(paymentKaKaoLogRepository).save(any());
    }

    @Test
    public void 카카오결제_실패_금액불일치() {
        // given
        KaKaoCompletePaymentRequest request = KaKaoCompletePaymentRequest.builder()
            .aid("123")
            .partnerUserId("1")
            .partnerOrderId("1")
            .paymentMethodType("CARD")
            .amount(Amount.builder().total(1000).build())
            .cardInfo(CardInfo.builder().build())
            .build();
        Long memberId = 1L;

        Payment payment = new Payment(request);
        ReflectionTestUtils.setField(payment, "id", 1L);
        given(paymentRepository.save(any()))
            .willReturn(payment);

        given(orderRepository.findById(any()))
            .willReturn(Optional.of(Orders.builder().totalPrice(100).build()));

        // when
        CompletePaymentDto completePaymentDto = paymentService.kakaoComplete(request, memberId);

        // then
        assertEquals(Message.BAD_REQUEST, completePaymentDto.getMessage());
        verify(paymentKaKaoLogRepository).save(any());
    }

    @Test
    public void 카카오결제_실패_회원불일치() {
        // given
        KaKaoCompletePaymentRequest request = KaKaoCompletePaymentRequest.builder()
            .aid("123")
            .partnerUserId("2")
            .partnerOrderId("1")
            .paymentMethodType("CARD")
            .amount(Amount.builder().total(1000).build())
            .cardInfo(CardInfo.builder().build())
            .build();
        Long memberId = 1L;

        Payment payment = new Payment(request);
        ReflectionTestUtils.setField(payment, "id", 1L);

        // when
        CompletePaymentDto completePaymentDto = paymentService.kakaoComplete(request, memberId);

        // then
        assertEquals(Message.BAD_REQUEST, completePaymentDto.getMessage());
        verify(paymentKaKaoLogRepository).save(any());
    }


}