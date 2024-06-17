package com.mung.product.dto;

import com.mung.common.domain.Validate;
import com.mung.common.request.BaseSearchRequest;
import com.mung.product.domain.Category;
import com.mung.product.domain.Product;
import com.mung.product.dto.CategoryDto.CategoryResponse;
import com.mung.product.dto.OptionsDto.OptionsResponse;
import com.mung.product.dto.OptionsDto.OptionsStockResponse;
import com.querydsl.core.annotations.QueryProjection;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;

public class ProductDto {

    @Getter
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class SearchProductCondition extends BaseSearchRequest {

        private Long id;
        private String name;
        private Long compId;
        private Boolean activeForSale;
        private List<Long> categoryIds = new ArrayList<>();
        private List<Long> optionIds = new ArrayList<>();

        @Builder
        public SearchProductCondition(Long id, String name, Long compId, Boolean activeForSale,
            List<Long> categoryIds, List<Long> optionIds) {
            this.id = id;
            this.name = name;
            this.compId = compId;
            this.activeForSale = activeForSale;
            this.categoryIds = categoryIds;
            this.optionIds = optionIds;
        }
    }

    @Data
    public static class ProductResponse {

        private Long id;
        private String name;
        private String details;
        private Long compId;
        private CategoryDto.CategoryResponse category;
        private List<OptionsResponse> options = new ArrayList<>();

        @Builder
        @QueryProjection
        public ProductResponse(Long id, String name, String details, Long compId,
            CategoryDto.CategoryResponse category, List<OptionsResponse> options) {
            this.id = id;
            this.name = name;
            this.details = details;
            this.compId = compId;
            this.category = category;
            this.options = options;
        }
    }

    @Data
    public static class ProductSearchResponse {

        private final List<OptionsResponse> options = new ArrayList<>();
        private Long id;
        private String name;
        private String details;
        private Long compId;
        private CategoryDto.CategoryResponse category;

        @QueryProjection
        public ProductSearchResponse(Product product, Category category) {
            this.id = product.getId();
            this.name = product.getName();
            this.details = product.getDetails();
            this.compId = product.getCompId();
            this.category = new CategoryResponse(category, category.getDepth());
        }
    }

    @Data
    public static class AddProductRequest {

        @NotBlank(message = Validate.Message.NOT_EMPTY)
        private String name;
        @NotBlank(message = Validate.Message.NOT_EMPTY)
        private String details;
        private int price;
        private Long compId;
        @NotNull(message = Validate.Message.NOT_EMPTY)
        private Long categoryId;

        @Builder
        public AddProductRequest(String name, String details, int price, Long compId,
            Long categoryId) {
            this.name = name;
            this.details = details;
            this.price = price;
            this.compId = compId;
            this.categoryId = categoryId;
        }
    }

    @Data
    public static class UpdateProductRequest {

        @NotNull(message = Validate.Message.NOT_EMPTY)
        private Long id;
        @NotBlank(message = Validate.Message.NOT_EMPTY)
        private String name;
        private int price;
        @NotBlank(message = Validate.Message.NOT_EMPTY)
        private String details;
        @NotNull(message = Validate.Message.NOT_EMPTY)
        private Long categoryId;
        @NotNull(message = Validate.Message.NOT_EMPTY)
        private Boolean activeForSale;

        @Builder
        public UpdateProductRequest(Long id, String name, int price, String details,
            Long categoryId, Boolean activeForSale) {
            this.id = id;
            this.name = name;
            this.price = price;
            this.details = details;
            this.categoryId = categoryId;
            this.activeForSale = activeForSale;
        }
    }

    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class DeleteProductRequest {

        @NotNull(message = Validate.Message.NOT_EMPTY)
        private Long id;

        @Builder
        public DeleteProductRequest(Long id) {
            this.id = id;
        }
    }

    @Data
    @NoArgsConstructor(access = AccessLevel.PROTECTED)
    public static class ProductStockResponse {

        private Long productId;
        private String name;
        private Long compId;
        private CategoryDto.CategoryResponse category;
        private List<OptionsStockResponse> options = new ArrayList<>();

        @Builder
        public ProductStockResponse(Long productId, String name, Long compId,
            CategoryResponse category, List<OptionsStockResponse> options) {
            this.productId = productId;
            this.name = name;
            this.compId = compId;
            this.category = category;
            this.options = options;
        }
    }

}
