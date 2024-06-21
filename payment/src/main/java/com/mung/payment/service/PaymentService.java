package com.mung.payment.service;

import com.mung.common.domain.PaymentProvider;
import com.mung.common.domain.Validate.Message;
import com.mung.common.exception.BadRequestException;
import com.mung.order.domain.OrderStatus;
import com.mung.order.domain.Orders;
import com.mung.order.dto.OrderDto.OrderRequest;
import com.mung.order.service.OrderService;
import com.mung.payment.domain.DecreaseStockEvent;
import com.mung.payment.domain.KakaopayPayment;
import com.mung.payment.domain.Payment;
import com.mung.payment.dto.KakaopayDto.KakaopayApproveResponse;
import com.mung.payment.dto.KakaopayDto.KakaopayCancelResponse;
import com.mung.payment.dto.KakaopayDto.KakaopayReadyResponse;
import com.mung.payment.dto.PaymentDto.CancelPaymentRequest;
import com.mung.payment.dto.PaymentDto.CancelPaymentResponse;
import com.mung.payment.dto.PaymentDto.CompletePaymentResponse;
import com.mung.payment.repository.KakaopayPaymentRedisRepository;
import com.mung.payment.repository.PaymentRepository;
import com.mung.stock.exception.OutOfStockException;
import java.util.Objects;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
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
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public KakaopayReadyResponse readyKakaoPayment(String agent, OrderRequest orderRequest, Long memberId) {
        // 1. 주문(결제 대기 :: PAYMENT_PENDING) 생성
        Orders order = orderService.requestOrder(orderRequest, memberId);

        // 2. 결제 금액 확인
        if (order.getTotalPrice() != orderRequest.getTotalPrice()) {
            log.error(
                "PaymentService.processKakaoPayment :: 주문번호 {} :: 금액이 일치하지 않습니다. order.totalPrice = {}, orderRequest.totalPrice= = {}",
                order.getId(), order.getTotalPrice(),
                orderRequest.getTotalPrice());
            throw new BadRequestException();
        }

        // 3. 카카오 결제 요청
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
        KakaopayApproveResponse approveResponse = null;
        String message = "결제 도중 에러가 발생했습니다.";
        try {
            // 1. 카카오 결제 승인 요청
            KakaopayPayment kakaopayPayment = kakaopayPaymentRedisRepository.findById(orderId)
                .orElseThrow(BadRequestException::new);

            approveResponse = kakaopayService.approve(pgToken, kakaopayPayment);

            // 2. Payment 저장
            paymentRepository.save(new Payment(approveResponse));

            // 3. 주문 상태 (결제 완료 :: PAYMENT_CONFIRMED) update
            Orders updatedOrder = orderService.updateOrderStatus(orderId, OrderStatus.PAYMENT_CONFIRMED);

            kakaopayPaymentRedisRepository.delete(kakaopayPayment);

            // 4. 재고 차감
            log.info("PaymentService.completeKakaoPayment :: 주문번호 {} :: 재고 차감 이벤트 발생", orderId);
            eventPublisher.publishEvent(new DecreaseStockEvent(orderId, memberId));
            
            return CompletePaymentResponse.builder()
                .orderId(updatedOrder.getId())
                .totalAmount(approveResponse.getAmount().getTotal())
                .build();

        } catch (Exception e) {
            log.error("PaymentService.completeKakaoPayment :: ", e);
            if (approveResponse != null) {
                kakaopayService.cancel(Payment.builder()
                    .tid(approveResponse.getTid())
                    .totalAmount(approveResponse.getAmount().getTotal())
                    .taxFree(Optional.ofNullable(approveResponse.getAmount().getTaxFree())
                        .orElse(0))
                    .build());
            }
            orderService.updateOrderStatus(orderId, OrderStatus.FAILED);

            if (e.getClass() == OutOfStockException.class) {
                message = Message.OUT_OF_STOCK;
            }
        }

        return CompletePaymentResponse.builder()
            .message(message)
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
            KakaopayCancelResponse kakaopayCancelResponse = kakaopayService.cancel(payment);
            response = CancelPaymentResponse.builder()
                .tid(kakaopayCancelResponse.getTid())
                .message(kakaopayCancelResponse.getStatus())
                .build();
        }

        return response;
    }

}
