package com.mung.payment.dto;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import com.mung.payment.dto.KakaopayDto.KakaopayApproveResponse.Amount;
import java.util.Date;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

public class KakaopayDto {

    @Data
    @Builder
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class KakaopayReadyRequest {

        private String cid;
        private String partnerOrderId;
        private String partnerUserId;
        private String itemName;
        private Integer quantity;
        private Integer totalAmount;
        private Integer taxFreeAmount;
        private Integer vatAmount;
        private String approvalUrl;
        private String cancelUrl;
        private String failUrl;
    }

    @Data
    @Builder
    @AllArgsConstructor
    public static class KakaopayReadyResponse {

        private String tid;
        private Boolean tms_result;
        private String created_at;
        private String next_redirect_pc_url;
        private String next_redirect_mobile_url;
        private String next_redirect_app_url;
        private String android_app_scheme;
        private String ios_app_scheme;
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class KakaopayApproveRequest {

        private String cid;
        private String tid;
        private String partnerOrderId;
        private String partnerUserId;
        private String pgToken;
    }

    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @AllArgsConstructor
    @Builder
    public static class KakaopayApproveResponse {

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
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    @AllArgsConstructor
    @Builder
    public static class KakaopayCancelRequest {

        private String cid;
        private String cidSecret;
        private String tid;
        private Integer cancelAmount;
        private int cancelTaxFreeAmount;
        private Integer cancelVatAmount;
        private Integer cancelAvailableAmount;
        private String payload;

    }

    @Data
    @AllArgsConstructor
    @Builder
    public static class KakaopayCancelResponse {

        private String aid;
        private String tid;
        private String cid;
        private String status;
        private String partnerUserId;
        private String partnerOrderId;
        private String paymentMethodType;
        private Amount amount;
        private Amount approvedCancelAmount;
        private Amount canceledAmount;
        private Amount cancelAvailableAmount;
        private String itemName;
        private String itemCode;
        private Integer quantity;
        private Date createdAt;
        private Date approvedAt;
        private Date canceledAt;
        private String payload;

    }

}
