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

        public int addCount(int count) {
            this.count += count;
            return this.count;
        }

        @Builder
        public AddCartDto(Long productId, Long optionId, int count) {
            this.productId = productId;
            this.optionId = optionId;
            this.count = count;
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

}
