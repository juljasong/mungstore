package com.mung.payment.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class PaymentDto {

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @AllArgsConstructor
    @Builder
    public static class KaKaoCompletePaymentRequest {
        private String aid;
        private String tid;
        private String cid;
        private String partnerUserId;
        private String partnerOrderId;
        private String paymentMethodType;
        private Amount amount;
        private CardInfo cardInfo;
        private String itemName;
        private String itemCode;
        private Integer quantity;
        private Date createdAt;
        private Date approvedAt;
        private String payload;

        @Data
        @AllArgsConstructor
        @Builder
        public static class Amount {
            private Integer total;
            private Integer taxFree;
            private Integer vat;
            private Integer point;
            private Integer discount;
            private Integer greenDeposit;
        }

        @Data
        @AllArgsConstructor
        @Builder
        public static class CardInfo {
            private String kakaopayPurchaseCorp;
            private String kakaopayPurchaseCorpCode;
            private String kakaopayIssuerCorp;
            private String kakaopayIssuerCorpCode;
            private String bin;
            private String cardType;
            private String installMonth;
            private String approvedId;
            private String cardMid;
            private String interestFreeInstall;
            private String installmentType;
            private String cardItemCode;
        }
    }

    @Data
    public static class CompletePaymentDto {
        private Long orderId;
        private int totalAmount;
        private String message;

        @Builder
        public CompletePaymentDto(Long orderId,int totalAmount, String message) {
            this.orderId = orderId;
            this.totalAmount = totalAmount;
            this.message = message;
        }
    }

    @Data
    public static class KaKaoCancelPaymentRequest {

    }
}
