package com.mung.product.dto;

import com.mung.common.domain.Validate;
import com.mung.product.domain.Category;
import jakarta.validation.constraints.NotBlank;
import java.util.List;
import java.util.stream.Collectors;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

public class CategoryDto {

    @Data
    public static class AddCategoryRequest {

        private Long parentId;

        @NotBlank(message = Validate.Message.NOT_EMPTY)
        private String name;

        @Builder
        public AddCategoryRequest(Long parentId, String name) {
            this.parentId = parentId;
            this.name = name;
        }
    }

    @Data
    public static class CategoryResponse {

        private Long id;
        private String name;
        private Integer depth;
        private CategoryResponse parent;

        public CategoryResponse(Category category, Integer depth) {
            this.id = category.getId();
            this.name = category.getName();
            this.depth = category.getDepth();

            if (depth > 0) {
                Category parent = category.getParent();
                if (parent != null) {
                    this.parent = new CategoryResponse(parent, depth - 1);
                }
            }
        }
    }

    @Data
    @AllArgsConstructor
    public static class CategoriesResponse {

        private Long id;
        private String name;
        private Integer depth;
        private List<CategoriesResponse> children;

        public static CategoriesResponse of(Category category) {
            return new CategoriesResponse(category.getId(),
                category.getName(),
                category.getDepth(),
                category.getChildren().stream().map(CategoriesResponse::of)
                    .collect(Collectors.toList())
            );
        }
    }

}
