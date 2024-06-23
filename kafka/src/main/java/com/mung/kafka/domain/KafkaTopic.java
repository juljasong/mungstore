package com.mung.kafka.domain;

import lombok.Getter;

@Getter
public enum KafkaTopic {

    PAYMENT_CANCEL_REQUEST("payment_cancel_request");

    private String topicName;

    KafkaTopic(String topicName) {
        this.topicName = topicName;
    }
}
