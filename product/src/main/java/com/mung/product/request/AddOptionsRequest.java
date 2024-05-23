package com.mung.product.request;

import com.mung.common.domain.Validate;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddOptionsRequest {

    @NotNull(message = Validate.MESSAGE.NOT_EMPTY)
    private Long productId;

    @Column(nullable = false)
    @NotBlank(message = Validate.MESSAGE.NOT_EMPTY)
    private String name;

    private Integer price;

    @Builder
    public AddOptionsRequest(Long productId, String name, Integer price) {
        this.productId = productId;
        this.name = name;
        this.price = price;
    }
}
