package com.mung.product.request;

import com.mung.common.domain.Validate;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddProductRequest {

    @Column(nullable = false)
    @NotBlank(message = Validate.MESSAGE.NOT_EMPTY)
    private String name;

    @Column(nullable = false)
    @NotBlank(message = Validate.MESSAGE.NOT_EMPTY)
    private String details;

    private Long compId;

    @Column(nullable = false)
    @NotNull(message = Validate.MESSAGE.NOT_EMPTY)
    private List<Long> categoryId;

    @Builder
    public AddProductRequest(String name, String details, Long compId, List<Long> categoryId) {
        this.name = name;
        this.details = details;
        this.compId = compId;
        this.categoryId = categoryId;
    }
}
