package com.mung.product.dto;

import com.mung.common.domain.Validate;
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

        @NotNull(message = Validate.MESSAGE.NOT_EMPTY)
        private Long productId;

        @NotBlank(message = Validate.MESSAGE.NOT_EMPTY)
        private String name;

        private int price;

        @Builder
        public AddOptionsRequest(Long productId, String name, int price) {
            this.productId = productId;
            this.name = name;
            this.price = price;
        }
    }

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class OptionsResponse {

        private Long id;
        private String name;
        private Integer price;

        @Builder
        public OptionsResponse(Long id, String name, Integer price) {
            this.id = id;
            this.name = name;
            this.price = price;
        }
    }
}
