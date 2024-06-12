package com.mung.order.domain;

import lombok.Getter;

@Getter
public enum DeliveryStatus {

    READY("준비 중"),
    SHIPPED("배송 중"),
    DELIVERED("배송 완료");

    private String kor;

    DeliveryStatus(String kor) {
        this.kor = kor;
    }
}
