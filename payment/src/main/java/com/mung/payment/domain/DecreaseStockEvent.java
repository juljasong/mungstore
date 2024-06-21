package com.mung.payment.domain;

import lombok.Getter;

@Getter
public class DecreaseStockEvent {

    private final Long orderId;
    private final Long memberId;

    public DecreaseStockEvent(Long orderId, Long memberId) {
        this.orderId = orderId;
        this.memberId = memberId;
    }
}
