package com.mung.payment.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.verify;

import com.mung.common.domain.PaymentProvider;
import com.mung.common.domain.Validate.Message;
import com.mung.common.exception.BadRequestException;
import com.mung.order.domain.Orders;
import com.mung.order.repository.OrderRepository;
import com.mung.payment.domain.Payment;
import com.mung.payment.dto.PaymentDto.CancelPaymentRequest;
import com.mung.payment.dto.PaymentDto.CancelPaymentResponse;
import com.mung.payment.dto.PaymentDto.CompletePaymentResponse;
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
        CompletePaymentResponse completePaymentDto = paymentService.kakaoComplete(request,
            memberId);

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
        CompletePaymentResponse completePaymentDto = paymentService.kakaoComplete(request,
            memberId);

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
        CompletePaymentResponse completePaymentDto = paymentService.kakaoComplete(request,
            memberId);

        // then
        assertEquals(Message.BAD_REQUEST, completePaymentDto.getMessage());
        verify(paymentKaKaoLogRepository).save(any());
    }

    @Test
    public void 결제취소_카카오_성공() {
        // given
        CancelPaymentRequest request = CancelPaymentRequest.builder().orderId(8L).build();
        Long memberId = 1L;

        Payment payment = Payment.builder()
            .tid("1234")
            .paymentProvider(PaymentProvider.KAKAO)
            .totalAmount(1000)
            .taxFree(0)
            .vat(200)
            .build();
        ReflectionTestUtils.setField(payment, "createdBy", 1L);
        given(paymentRepository.findByOrderId(any()))
            .willReturn(Optional.of(payment));

        // when
        CancelPaymentResponse response = paymentService.cancelPayment(request, memberId);

        // then
        assertEquals("1234", response.getTid());
    }

    @Test
    public void 결제취소_카카오_실패_주문자불일치() {
        // given
        CancelPaymentRequest request = CancelPaymentRequest.builder().orderId(8L).build();
        Long memberId = 2L;

        Payment payment = Payment.builder()
            .tid("1234")
            .paymentProvider(PaymentProvider.KAKAO)
            .totalAmount(1000)
            .taxFree(0)
            .vat(200)
            .build();
        ReflectionTestUtils.setField(payment, "createdBy", 1L);
        given(paymentRepository.findByOrderId(any()))
            .willReturn(Optional.of(payment));

        // expected
        assertThrows(BadRequestException.class,
            () -> paymentService.cancelPayment(request, memberId));
    }
}