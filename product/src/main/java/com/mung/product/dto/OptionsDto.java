package com.mung.product.dto;

import com.mung.common.domain.Validate;
import com.mung.common.domain.Validate.Message;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class OptionsDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
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

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
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
}
