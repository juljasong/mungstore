package com.mung.consumer.kafka;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mung.kafka.dto.PaymentCancelDto.PaymentCancelRequest;
import com.mung.payment.dto.PaymentDto.CancelPaymentRequest;
import com.mung.payment.dto.PaymentDto.CancelPaymentResponse;
import com.mung.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentCancelRequestConsumer {

    private final ObjectMapper objectMapper;
    private final PaymentService paymentService;

    @KafkaListener(topics = "payment_cancel_request", groupId = "mungstore")
    public void paymentCancelRequestTopicConsumer(String message) {
        try {
            log.info("paymentCancelRequestTopicConsumer request :: message = {}", message);
            PaymentCancelRequest request = objectMapper.readValue(message,
                PaymentCancelRequest.class);

            CancelPaymentResponse response = paymentService.cancelPayment(
                CancelPaymentRequest.builder()
                    .orderId(request.getOrderId())
                    .build(),
                request.getMemberId());

            log.info("paymentCancelRequestTopicConsumer response :: message = {}", response.toString());

        } catch (JsonProcessingException e) {
            log.error("PaymentCancelRequestConsumer.paymentCancelRequestTopicConsumer :: ", e);
            throw new RuntimeException();
        }
    }

}
