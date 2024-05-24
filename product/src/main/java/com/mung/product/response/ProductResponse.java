package com.mung.product.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductResponse {

    private Long id;

    private String name;

    private String details;

    private Long compId;

    private List<CategoryResponse> categories = new ArrayList<>();

    private List<OptionsResponse> options = new ArrayList<>();

    @Builder
    public ProductResponse(Long id, String name, String details, Long compId, List<CategoryResponse> categories, List<OptionsResponse> options) {
        this.id = id;
        this.name = name;
        this.details = details;
        this.compId = compId;
        this.categories = categories;
        this.options = options;
    }
}
