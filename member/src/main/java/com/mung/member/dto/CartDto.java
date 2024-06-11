package com.mung.member.dto;

import java.util.Objects;
import lombok.Builder;
import lombok.Data;

public class CartDto {

    @Data
    public static class AddCartDto {

        private Long productId;
        private Long optionId;
        private int count;

        @Builder
        public AddCartDto(Long productId, Long optionId, int count) {
            this.productId = productId;
            this.optionId = optionId;
            this.count = count;
        }

        public int addCount(int count) {
            this.count += count;
            return this.count;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || getClass() != o.getClass()) {
                return false;
            }
            AddCartDto cartDto = (AddCartDto) o;
            return Objects.equals(productId, cartDto.productId) && Objects.equals(
                optionId, cartDto.optionId);
        }

        @Override
        public int hashCode() {
            return Objects.hash(productId, optionId);
        }

    }

    @Data
    public static class DeleteCartDto {

        private Long productId;
        private Long optionId;

        @Builder
        public DeleteCartDto(Long productId, Long optionId) {
            this.productId = productId;
            this.optionId = optionId;
        }

    }

    @Data
    public static class CartResponse {

        private Long productId;
        private Long optionId;

        private String productName;
        private int productPrice;

        private String optionName;
        private int optionPrice;
        private int count;

        private int totalPrice;
        private Boolean activeForSale;
        private Boolean useYn;

        public CartResponse(Object[] object, int count) {
            this.productId = (Long) object[0];
            this.optionId = (Long) object[5];
            this.productName = (String) object[1];
            this.productPrice = (int) object[2];
            this.optionName = (String) object[6];
            this.optionPrice = (int) object[7];
            this.count = count;
            this.totalPrice = this.productPrice + this.optionPrice;
            this.activeForSale = (Boolean) object[3];
            this.useYn = (Boolean) object[4];
        }
    }

}
