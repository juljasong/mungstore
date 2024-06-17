package com.mung.payment.service;

import com.mung.common.exception.BadRequestException;
import com.mung.order.domain.OrderStatus;
import com.mung.order.domain.Orders;
import com.mung.order.repository.OrderRepository;
import com.mung.payment.domain.Payment;
import com.mung.payment.domain.PaymentKakaoLog;
import com.mung.payment.dto.PaymentDto.CompletePaymentDto;
import com.mung.payment.dto.PaymentDto.KaKaoCancelPaymentRequest;
import com.mung.payment.dto.PaymentDto.KaKaoCompletePaymentRequest;
import com.mung.payment.repository.PaymentKaKaoLogRepository;
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
    private final PaymentKaKaoLogRepository paymentKaKaoLogRepository;
    private final OrderRepository orderRepository;

    @Transactional
    public CompletePaymentDto kakaoComplete(KaKaoCompletePaymentRequest request, Long memberId) {

        if (!Long.valueOf(request.getPartnerUserId()).equals(memberId)) {
            throw new BadRequestException();
        }

        Payment savedPayment = paymentRepository.save(new Payment(request));
        paymentKaKaoLogRepository.nonDuplicatedSave(new PaymentKakaoLog(request));

        Orders order = orderRepository.findById(Long.valueOf(request.getPartnerOrderId()))
            .orElseThrow(BadRequestException::new);

        if (order.getTotalPrice() != request.getAmount().getTotal()) {
            log.error("주문번호 :: " + request.getPartnerOrderId() + ":: 금액이 일치하지 않습니다.");
            throw new BadRequestException();
        }

        order.updateStatus(OrderStatus.PAYMENT_CONFIRMED);

        return CompletePaymentDto.builder()
            .orderId(savedPayment.getOrderId())
            .totalAmount(savedPayment.getTotalAmount())
            .build();
    }

    @Transactional
    public String kakaoCancel(KaKaoCancelPaymentRequest request, Long memberId) {

        return null;
    }
}
