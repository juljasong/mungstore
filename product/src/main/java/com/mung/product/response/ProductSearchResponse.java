package com.mung.product.response;

import com.mung.product.domain.Category;
import com.mung.product.domain.Product;
import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductSearchResponse {

    private Long id;

    private String name;

    private String details;

    private Long compId;

    private List<CategoryResponse> categories = new ArrayList<>();

    private List<OptionsResponse> options = new ArrayList<>();

    @QueryProjection
    public ProductSearchResponse(Product product, List<Category> categoryResponseList) {
        this.id = product.getId();
        this.name = product.getName();
        this.details = product.getDetails();
        this.compId = product.getCompId();
        categoryResponseList.forEach(
                category -> this.categories.add(
                        CategoryResponse.builder()
                                .id(category.getId())
                                .name(category.getName())
                                .parentId(category.getParent().getId())
                                .parentName(category.getParent().getName())
                                .build()
                )
        );
    }


}
