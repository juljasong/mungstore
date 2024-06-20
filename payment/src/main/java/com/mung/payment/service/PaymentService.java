package com.mung.payment.service;

import com.mung.common.exception.BadRequestException;
import com.mung.order.domain.OrderStatus;
import com.mung.order.domain.Orders;
import com.mung.order.dto.OrderDto.OrderRequest;
import com.mung.order.service.OrderService;
import com.mung.payment.domain.KakaopayPayment;
import com.mung.payment.domain.Payment;
import com.mung.payment.dto.KakaopayDto.KakaopayApproveResponse;
import com.mung.payment.dto.KakaopayDto.KakaopayReadyResponse;
import com.mung.payment.dto.PaymentDto.CompletePaymentResponse;
import com.mung.payment.repository.KakaopayPaymentRedisRepository;
import com.mung.payment.repository.PaymentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentService {

    private final PaymentRepository paymentRepository;
    private final KakaopayPaymentRedisRepository kakaopayPaymentRedisRepository;

    private final OrderService orderService;
    private final KakaopayService kakaopayService;

    @Transactional
    public KakaopayReadyResponse readyKakaoPayment(String agent, OrderRequest orderRequest,
        Long memberId) {
        // 1. 주문(결제 대기 :: PAYMENT_PENDING) 생성
        Orders order = orderService.requestOrder(orderRequest, memberId);
        // 2. 카카오 결제 요청
        KakaopayReadyResponse response = kakaopayService.ready(agent, orderRequest, order, memberId);
        kakaopayPaymentRedisRepository.save(KakaopayPayment.builder()
            .tid(response.getTid())
            .memberId(memberId)
            .orderId(order.getId())
            .build());
        return response;
    }

    @Transactional
    public CompletePaymentResponse completeKakaoPayment(String agent, String pgToken, Long memberId, Long orderId) {
        try {
            // 1. 카카오 결제 승인 요청
            KakaopayPayment kakaopayPayment = kakaopayPaymentRedisRepository.findById(orderId)
                .orElseThrow(BadRequestException::new);

            KakaopayApproveResponse approveResponse = kakaopayService.approve(pgToken,
                kakaopayPayment);

            // 2. 결제 금액 확인
            Orders order = orderService.getOrder(orderId, memberId);
            if (order.getTotalPrice() != approveResponse.getAmount().getTotal()) {
                log.error(
                    "PaymentService.processKakaoPayment :: 주문번호 {} :: 금액이 일치하지 않습니다. order.totalPrice = {}, request.total() = {}",
                    approveResponse.getPartnerOrderId(), order.getTotalPrice(),
                    approveResponse.getAmount().getTotal());
                throw new BadRequestException();
            }

            // 3. Payment 저장
            paymentRepository.save(new Payment(approveResponse));

            // 4. 주문 상태 (결제 완료 :: PAYMENT_CONFIRMED) update
            Orders updatedOrder = orderService.updateOrderStatus(orderId, OrderStatus.PAYMENT_CONFIRMED);

            kakaopayPaymentRedisRepository.delete(kakaopayPayment);
            return CompletePaymentResponse.builder()
                .orderId(updatedOrder.getId())
                .totalAmount(approveResponse.getAmount().getTotal())
                .build();

        } catch (Exception e) {
            log.error("PaymentService.completeKakaoPayment :: ", e);
            // TODO :: 결제 취소
        }

        return CompletePaymentResponse.builder()
            .message("결제 도중 에러가 발생했습니다.")
            .build();
    }

//
//    @Transactional
//    public CancelPaymentResponse cancelPayment(CancelPaymentRequest request, Long memberId) {
//        Payment payment = paymentRepository.findByOrderId(request.getOrderId())
//            .orElseThrow(BadRequestException::new);
//
//        if (!Objects.equals(payment.getCreatedBy(), memberId)) {
//            log.error(
//                "PaymentService.cancelPayment :: 주문자 불일치 payment.createdBy = {}, memberId = {}",
//                payment.getCreatedBy(), memberId);
//            throw new BadRequestException();
//        }
//
//        CancelPaymentResponse response = null;
//        if (payment.getPaymentProvider() == PaymentProvider.KAKAO) {
//            response = cancelKaKao(payment);
//        }
//
//        return response;
//    }
//
//    @Transactional
//    private CancelPaymentResponse cancelKaKao(Payment payment) {
//        KakaoCancelPaymentResponse response = feignKakaoCancel(KaKaoCancelPaymentRequest.builder()
//            .cid("가맹점코드")
//            .tid(payment.getTid())
//            .cancelAmount(payment.getTotalAmount())
//            .cancelTaxFreeAmount(
//                payment.getTaxFree())  // 비과세(tax_free_amount)와 부가세(vat_amount)를 맞게 요청해야 함
//            .cancelVatAmount(payment.getVat())
//            .build());
//
//        if (response.getStatus().equals(KakaoPayStatus.CANCEL_PAYMENT.name())) {
//            payment.updateStatus(PaymentStatus.CANCELLED);
//        } else {
//            log.error("PaymentService.cancelKaKao :: 카카오 취소 실패 tid = {}, status = {}",
//                response.getTid(), response.getStatus());
//            throw new BadRequestException();
//        }
//
//        return CancelPaymentResponse.builder()
//            .message(response.getStatus())
//            .tid(payment.getTid())
//            .build();
//    }
//
//    private KakaoCancelPaymentResponse feignKakaoCancel(KaKaoCancelPaymentRequest request) {
//        // TODO :: Feign 으로 결제 취소 요청 날리기 -> KakaoCancelPaymentResponse 받기
//        try {
//            return KakaoCancelPaymentResponse.builder()
//                .tid("T1234567890123456789")
//                .cid("TC0ONETIME")
//                .status("CANCEL_PAYMENT")
//                .partnerUserId("partner_order_id")
//                .partnerOrderId("partner_user_id")
//                .paymentMethodType("MONEY")
//                .itemName("초코파이")
//                .amount(Amount.builder()
//                    .total(3900)
//                    .taxFree(0)
//                    .vat(200)
//                    .point(0)
//                    .discount(0)
//                    .greenDeposit(0)
//                    .build())
//                .approvedCancelAmount(Amount.builder()
//                    .total(3900)
//                    .taxFree(0)
//                    .vat(200)
//                    .point(0)
//                    .discount(0)
//                    .greenDeposit(0)
//                    .build())
//                .canceledAmount(Amount.builder()
//                    .total(3900)
//                    .taxFree(0)
//                    .vat(200)
//                    .point(0)
//                    .discount(0)
//                    .greenDeposit(0)
//                    .build())
//                .cancelAvailableAmount(Amount.builder().build())
//                .canceledAt(
//                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2023-07-15T21:18:22"))
//                .approvedAt(
//                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2023-07-15T21:18:22"))
//                .canceledAt(
//                    new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss").parse("2023-07-15T21:18:22"))
//                .build();
//        } catch (ParseException e) {
//            log.error("PaymentService.feignKakaoCancel", e);
//        }
//        return null;
//    }


}
