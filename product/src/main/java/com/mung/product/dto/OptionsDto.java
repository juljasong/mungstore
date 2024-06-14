package com.mung.product.dto;

import com.mung.common.domain.Validate;
import com.mung.common.domain.Validate.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Builder;
import lombok.Data;

public class OptionsDto {

    @Data
    public static class AddOptionsRequest {

        @NotNull(message = Validate.Message.NOT_EMPTY)
        private Long productId;

        @NotBlank(message = Validate.Message.NOT_EMPTY)
        private String name;

        private int price;

        @NotNull(message = Message.NOT_EMPTY)
        private Boolean available;

        @Builder
        public AddOptionsRequest(Long productId, String name, int price, Boolean available) {
            this.productId = productId;
            this.name = name;
            this.price = price;
            this.available = available;
        }
    }

    @Data
    public static class OptionsResponse {

        private Long id;
        private String name;
        private Integer price;
        private Boolean available;

        @Builder
        public OptionsResponse(Long id, String name, Integer price, Boolean available) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.available = available;
        }
    }

    @Data
    public static class OptionsStockResponse {

        private Long id;
        private String name;
        private Integer price;
        private Boolean available;
        private String skuId;
        private int quantity;

        @Builder
        public OptionsStockResponse(Long id, String name, Integer price, Boolean available,
            String skuId,
            int quantity) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.available = available;
            this.skuId = skuId;
            this.quantity = quantity;
        }
    }


}
