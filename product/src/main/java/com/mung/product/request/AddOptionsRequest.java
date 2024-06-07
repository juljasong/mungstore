package com.mung.product.request;

import com.mung.common.domain.Validate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddOptionsRequest {

    @NotNull(message = Validate.Message.NOT_EMPTY)
    private Long productId;

    @NotBlank(message = Validate.Message.NOT_EMPTY)
    private String name;

    private int price;

    @Builder
    public AddOptionsRequest(Long productId, String name, int price) {
        this.productId = productId;
        this.name = name;
        this.price = price;
    }
}
