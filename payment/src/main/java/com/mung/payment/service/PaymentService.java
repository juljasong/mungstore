package com.mung.payment.service;

import com.mung.common.domain.KakaoPayStatus;
import com.mung.common.domain.PaymentProvider;
import com.mung.common.domain.PaymentStatus;
import com.mung.common.domain.Validate.Message;
import com.mung.common.exception.BadRequestException;
import com.mung.order.domain.OrderStatus;
import com.mung.order.domain.Orders;
import com.mung.order.repository.OrderRepository;
import com.mung.payment.domain.Payment;
import com.mung.payment.domain.PaymentKakaoLog;
import com.mung.payment.dto.PaymentDto.CancelPaymentRequest;
import com.mung.payment.dto.PaymentDto.CancelPaymentResponse;
import com.mung.payment.dto.PaymentDto.CompletePaymentResponse;
import com.mung.payment.dto.PaymentDto.KaKaoCancelPaymentRequest;
import com.mung.payment.dto.PaymentDto.KaKaoCompletePaymentRequest;
import com.mung.payment.dto.PaymentDto.KaKaoCompletePaymentRequest.Amount;
import com.mung.payment.dto.PaymentDto.KakaoCancelPaymentResponse;
import com.mung.payment.repository.PaymentKaKaoLogRepository;
import com.mung.payment.repository.PaymentRepository;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final PaymentKaKaoLogRepository paymentKaKaoLogRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public CompletePaymentResponse kakaoComplete(KaKaoCompletePaymentRequest request,
        Long memberId) {
        CompletePaymentResponse completePaymentDto = null;
        try {
            completePaymentDto = processKakaoPayment(request, memberId);
        } catch (BadRequestException e) {
            completePaymentDto = handleBadRequsetException(e);
            // TODO: cancel 추가 (status 업데이트 + 결제 취소)
        } catch (Exception e) {
            completePaymentDto = handleException(e);
            // TODO: cancel 추가 (status 업데이트 + 결제 취소)
        } finally {
            paymentKaKaoLogRepository.save(new PaymentKakaoLog(request));
        }
        return completePaymentDto;
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    private CompletePaymentResponse processKakaoPayment(KaKaoCompletePaymentRequest request,
        Long memberId) {
        if (!Long.valueOf(request.getPartnerUserId()).equals(memberId)) {
            log.error(
                "PaymentService.processKakaoPayment :: 회원 불일치 request.userId() = {}, memberId = {}",
                request.getPartnerUserId(), memberId);
            throw new BadRequestException();
        }

        Payment savedPayment = paymentRepository.save(new Payment(request));
        Orders order = orderRepository.findById(Long.valueOf(request.getPartnerOrderId()))
            .orElseThrow(BadRequestException::new);

        if (order.getTotalPrice() != request.getAmount().getTotal()) {
            log.error(
                "PaymentService.processKakaoPayment :: 주문번호 {} 금액이 일치하지 않습니다. order.totalPrice = {}, request.total()",
                request.getPartnerOrderId(), order.getTotalPrice(), request.getAmount().getTotal());
            throw new BadRequestException();
        }

        order.updateStatus(OrderStatus.PAYMENT_CONFIRMED);

        return CompletePaymentResponse.builder()
            .orderId(savedPayment.getOrderId())
            .totalAmount(savedPayment.getTotalAmount())
            .build();
    }

    @Transactional
    public CancelPaymentResponse cancelPayment(CancelPaymentRequest request, Long memberId) {
        Payment payment = paymentRepository.findByOrderId(request.getOrderId())
            .orElseThrow(BadRequestException::new);

        if (!Objects.equals(payment.getCreatedBy(), memberId)) {
            log.error(
                "PaymentService.cancelPayment :: 주문자 불일치 payment.createdBy = {}, memberId = {}",
                payment.getCreatedBy(), memberId);
            throw new BadRequestException();
        }

        CancelPaymentResponse response = null;
        if (payment.getPaymentProvider() == PaymentProvider.KAKAO) {
            response = cancelKaKao(payment);
        }

        return response;
    }

    @Transactional
    private CancelPaymentResponse cancelKaKao(Payment payment) {
        KakaoCancelPaymentResponse response = feignKakaoCancel(KaKaoCancelPaymentRequest.builder()
            .cid("가맹점코드")
            .tid(payment.getTid())
            .cancelAmount(payment.getTotalAmount())
            .cancelTaxFreeAmount(
                payment.getTaxFree())  // 비과세(tax_free_amount)와 부가세(vat_amount)를 맞게 요청해야 함
            .cancelVatAmount(payment.getVat())
            .build());

        if (response.getStatus().equals(KakaoPayStatus.CANCEL_PAYMENT.name())) {
            payment.updateStatus(PaymentStatus.CANCELLED);
        } else {
            log.error("PaymentService.cancelKaKao :: 카카오 취소 실패 tid = {}, status = {}",
                response.getTid(), response.getStatus());
            throw new BadRequestException();
        }

        return CancelPaymentResponse.builder()
            .message(response.getStatus())
            .tid(payment.getTid())
            .build();
    }

    private KakaoCancelPaymentResponse feignKakaoCancel(KaKaoCancelPaymentRequest request) {
        // TODO :: Feign 으로 결제 취소 요청 날리기 -> KakaoCancelPaymentResponse 받기
        try {
            return KakaoCancelPaymentResponse.builder()
                .tid("T1234567890123456789")
                .cid("TC0ONETIME")
                .status("CANCEL_PAYMENT")
                .partnerUserId("partner_order_id")
                .partnerOrderId("partner_user_id")
                .paymentMethodType("MONEY")
                .itemName("초코파이")
                .amount(Amount.builder()
                    .total(3900)
                    .taxFree(0)
                    .vat(200)
                    .point(0)
                    .discount(0)
                    .greenDeposit(0)
                    .build())
                .approvedCancelAmount(Amount.builder()
                    .total(3900)
                    .taxFree(0)
                    .vat(200)
                    .point(0)
                    .discount(0)
                    .greenDeposit(0)
                    .build())
                .canceledAmount(Amount.builder()
                    .total(3900)
                    .taxFree(0)
                    .vat(200)
                    .point(0)
                    .discount(0)
                    .greenDeposit(0)
                    .build())
                .cancelAvailableAmount(Amount.builder().build())
                .canceledAt(
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2023-07-15T21:18:22"))
                .approvedAt(
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2023-07-15T21:18:22"))
                .canceledAt(
                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2023-07-15T21:18:22"))
                .build();
        } catch (ParseException e) {
            log.error("PaymentService.feignKakaoCancel", e);
        }
        return null;
    }

    private CompletePaymentResponse handleBadRequsetException(BadRequestException e) {
        log.error("PaymentService.kakaoComplete :: ", e);
        return CompletePaymentResponse.builder()
            .message(Message.BAD_REQUEST)
            .build();
    }

    private CompletePaymentResponse handleException(Exception e) {
        log.error("PaymentService.kakaoComplete :: ", e);
        return CompletePaymentResponse.builder()
            .message(HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .build();
    }

}
