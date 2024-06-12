package com.mung.order.dto;

import com.mung.common.domain.Address;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

public class OrderDto {

    @Data
    public static class OrderRequest {
        private List<Order> orders;
        private int totalPrice;
        private String tel1;
        private String tel2;
        private Address address;

        @Builder
        public OrderRequest(List<Order> orders, int totalPrice, String tel1, String tel2,
            Address address) {
            this.orders = orders;
            this.totalPrice = totalPrice;
            this.tel1 = tel1;
            this.tel2 = tel2;
            this.address = address;
        }
    }

    @Data
    public static class Order {
        private Long productId;
        private String productName;
        private Long optionId;
        private int quantity;
        private String contents;
        private int orderPrice;

        @Builder
        public Order(Long productId, String productName, Long optionId, int quantity,
            String contents,
            int orderPrice) {
            this.productId = productId;
            this.productName = productName;
            this.optionId = optionId;
            this.quantity = quantity;
            this.contents = contents;
            this.orderPrice = orderPrice;
        }
    }

    @Data
    public static class OrderResponse {
        private Long orderId;

        @Builder
        public OrderResponse(Long orderId) {
            this.orderId = orderId;
        }
    }

    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class OrderCancelRequest {
        private Long orderId;

        @Builder
        public OrderCancelRequest(Long orderId) {
            this.orderId = orderId;
        }
    }

}
