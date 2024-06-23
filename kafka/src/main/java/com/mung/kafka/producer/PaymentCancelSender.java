package com.mung.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mung.kafka.domain.KafkaTopic;
import com.mung.kafka.dto.PaymentCancelDto.PaymentCancelRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class PaymentCancelSender {

    private final KafkaTemplate<Integer, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void sendMessage(KafkaTopic topic, PaymentCancelRequest request) {
        try {
            kafkaTemplate.send(topic.getTopicName(), objectMapper.writeValueAsString(request));
        } catch (JsonProcessingException e) {
            log.error("PaymentCancelSender.sendMessage :: ", e);
            throw new RuntimeException();
        }
    }

}
