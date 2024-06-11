package com.mung.product.service;

import com.mung.common.exception.BadRequestException;
import com.mung.product.domain.Category;
import com.mung.product.domain.Product;
import com.mung.product.dto.CategoryDto.CategoryResponse;
import com.mung.product.dto.OptionsDto.OptionsResponse;
import com.mung.product.dto.ProductDto.AddProductRequest;
import com.mung.product.dto.ProductDto.DeleteProductRequest;
import com.mung.product.dto.ProductDto.ProductResponse;
import com.mung.product.dto.ProductDto.ProductSearchResponse;
import com.mung.product.dto.ProductDto.SearchProductCondition;
import com.mung.product.dto.ProductDto.UpdateProductRequest;
import com.mung.product.repository.ProductRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final CategoryService categoryService;

    @Transactional
    public void addProduct(AddProductRequest request) {
        Category category = categoryService.getCategory(request.getCategoryId());

        productRepository.save(Product.builder()
            .name(request.getName())
            .details(request.getDetails())
            .price(request.getPrice())
            .compId(request.getCompId())
            .activeForSale(true)
            .useYn(true)
            .category(category)
            .build());
    }

    public Product getProduct(Long productId) {
        return productRepository.findByIdAndUseYn(productId, true)
            .orElseThrow(BadRequestException::new);
    }

    public List<Object[]> getProductIdAndOptionId(Long productId, Long optionId) {
        return productRepository.findByIdAndOptionId(productId, optionId);
    }

    public ProductResponse getProductResponse(Long productId) {
        Product product = productRepository.findByIdAndUseYn(productId, true)
            .orElseThrow(BadRequestException::new);

        return ProductResponse.builder()
            .id(product.getId())
            .name(product.getName())
            .details(product.getDetails())
            .compId(product.getCompId())
            .category(getCategoryResponse(product))
            .options(getOptionsResponses(product))
            .build();
    }

    @Transactional
    public void updateProduct(UpdateProductRequest request) {
        Category category = categoryService.getCategory(request.getCategoryId());

        Product product = productRepository.findByIdAndUseYn(request.getId(), true)
            .orElseThrow(BadRequestException::new);

        product.updateProduct(request.getName(),
            request.getPrice(),
            request.getDetails(),
            request.getActiveForSale(),
            category
        );

    }

    @Transactional
    public void deleteProduct(DeleteProductRequest request) {
        Product product = productRepository.findByIdAndUseYn(request.getId(), true)
            .orElseThrow(BadRequestException::new);
        product.deleteProduct(request.getId());
    }

    private CategoryResponse getCategoryResponse(Product product) {
        Category category = product.getCategory();

        return new CategoryResponse(category, category.getDepth());
    }

    private List<OptionsResponse> getOptionsResponses(Product product) {
        return product.getOptions().stream()
            .map(o -> OptionsResponse.builder()
                .id(o.getId())
                .name(o.getName())
                .price(o.getPrice())
                .available(o.getAvailable())
                .build())
            .collect(Collectors.toList());
    }

    public Page<ProductSearchResponse> searchProduct(SearchProductCondition condition) {
        PageRequest pageRequest = PageRequest.of(condition.getPageNumber(),
            condition.getPageSize());

        return productRepository.search(condition, pageRequest);
    }

}
