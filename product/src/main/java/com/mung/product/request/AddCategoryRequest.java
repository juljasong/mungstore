package com.mung.product.request;

import com.mung.common.domain.Validate;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AddCategoryRequest {

    private Long parentId;

    @Column(nullable = false)
    @NotBlank(message = Validate.MESSAGE.NOT_EMPTY)
    private String name;

    @Builder
    public AddCategoryRequest(Long parentId, String name) {
        this.parentId = parentId;
        this.name = name;
    }
}
