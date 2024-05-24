package com.mung.product.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OptionsResponse {

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
