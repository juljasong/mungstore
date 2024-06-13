package com.mung.order.domain;

import lombok.Getter;

@Getter
public enum OrderStatus {

    PAYMENT_PENDING("결제 대기"),
    PAYMENT_CONFIRMED("결제 완료"),
    ORDER_CONFIRMED("주문 확인 중"),
    PREPARE_DELIVERY("배송 준비 중"),
    SHIPPED("배송 중"),
    DELIVERED("배송 완료"),
    PARTIALLY_DELIVERED("부분 배송 완료"),
    RETURN_REQUESTED("반품 요청"),
    RETURNED("반품 완료"),
    CANCEL_REQUESTED("주문 취소 요청"),
    CANCELLED("주문 취소"),
    REFUND_REQUESTED("환불 요청"),
    REFUNDED("환불 완료"),
    FAILED("결제/주문 실패");

    private String kor;

    OrderStatus(String kor) {
        this.kor = kor;
    }

}
