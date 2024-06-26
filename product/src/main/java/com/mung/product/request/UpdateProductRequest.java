package com.mung.product.request;

import com.mung.common.domain.Validate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateProductRequest {

    @NotNull(message = Validate.Message.NOT_EMPTY)
    private Long id;

    @NotBlank(message = Validate.Message.NOT_EMPTY)
    private String name;

    private int price;

    @NotBlank(message = Validate.Message.NOT_EMPTY)
    private String details;

    @NotNull(message = Validate.Message.NOT_EMPTY)
    private List<Long> categoryId = new ArrayList<>();

    @NotNull(message = Validate.Message.NOT_EMPTY)
    private Boolean activeForSale;

    @Builder
    public UpdateProductRequest(Long id, String name, int price, String details, List<Long> categoryId, Boolean activeForSale) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.details = details;
        this.categoryId = categoryId;
        this.activeForSale = activeForSale;
    }
}
