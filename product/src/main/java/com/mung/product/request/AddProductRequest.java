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
public class AddProductRequest {

    @NotBlank(message = Validate.Message.NOT_EMPTY)
    private String name;

    @NotBlank(message = Validate.Message.NOT_EMPTY)
    private String details;

    private int price;

    private Long compId;

    @NotNull(message = Validate.Message.NOT_EMPTY)
    private List<Long> categoryId = new ArrayList<>();

    @Builder
    public AddProductRequest(String name, String details, int price, Long compId, List<Long> categoryId) {
        this.name = name;
        this.details = details;
        this.price = price;
        this.compId = compId;
        this.categoryId = categoryId;
    }
}
