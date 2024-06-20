package com.mung.payment.dto;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class PaymentDto {

    @Data
    public static class CompletePaymentResponse {

        private Long orderId;
        private int totalAmount;
        private String message;

        @Builder
        public CompletePaymentResponse(Long orderId, int totalAmount, String message) {
            this.orderId = orderId;
            this.totalAmount = totalAmount;
            this.message = message;
        }
    }

    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    @AllArgsConstructor
    @Builder
    public static class CancelPaymentRequest {

        private Long orderId;
    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class CancelPaymentResponse {

        private String message;
        private String tid;
    }

}
