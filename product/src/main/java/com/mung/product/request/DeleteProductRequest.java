package com.mung.product.request;

import com.mung.common.domain.Validate;
import jakarta.validation.constraints.NotNull;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class DeleteProductRequest {

    @NotNull(message = Validate.Message.NOT_EMPTY)
    private Long id;

    @Builder
    public DeleteProductRequest(Long id) {
        this.id = id;
    }
}
