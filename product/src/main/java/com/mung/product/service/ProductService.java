package com.mung.product.service;

import com.mung.product.domain.Product;
import com.mung.product.domain.ProductCategory;
import com.mung.product.domain.ProductLog;
import com.mung.product.repository.ProductCategoryRepository;
import com.mung.product.repository.ProductLogRepository;
import com.mung.product.repository.ProductRepository;
import com.mung.product.request.AddProductRequest;
import com.mung.product.request.DeleteProductRequest;
import com.mung.product.request.SearchProductCondition;
import com.mung.product.request.UpdateProductRequest;
import com.mung.product.response.CategoryResponse;
import com.mung.product.response.OptionsResponse;
import com.mung.product.response.ProductResponse;
import com.mung.product.response.ProductSearchResponse;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductLogRepository productLogRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final CategoryService categoryService;
    private final EntityManager em;

    @Transactional
    public void addProduct(AddProductRequest request) throws BadRequestException {
        Product product = Product.builder()
                .name(request.getName())
                .details(request.getDetails())
                .price(request.getPrice())
                .compId(request.getCompId())
                .activeForSale(true)
                .build();
        productRepository.save(product);

        createCategoryAssociation(request.getCategoryId(), product);
        logProduct(product);
    }

    public Product getProduct(Long productId) throws BadRequestException {
        return productRepository.findByIdAndUseYn(productId, true)
                .orElseThrow(BadRequestException::new);
    }

    public ProductResponse getProductResponse(Long productId) throws BadRequestException {
        Product product = productRepository.findByIdAndUseYn(productId, true)
                .orElseThrow(BadRequestException::new);

        return ProductResponse.builder()
                .id(product.getId())
                .name(product.getName())
                .details(product.getDetails())
                .compId(product.getCompId())
                .categories(getCategoryResponseList(product))
                .options(getOptionsResponses(product))
                .build();
    }

    @Transactional
    public void updateProduct(UpdateProductRequest request) throws BadRequestException {
        Product product = productRepository.findByIdAndUseYn(request.getId(), true)
                .orElseThrow(BadRequestException::new);
        List<Long> productCategories = new ArrayList<>();
        List<Long> requestCategories = new ArrayList<>(request.getCategoryId());
        product.getCategories().forEach(c -> productCategories.add(c.getCategory().getId()));

        Collections.sort(productCategories);
        Collections.sort(requestCategories);

        if (!request.getCategoryId().equals(productCategories)) {
            product.getCategories().forEach(
                    category -> {
                        productCategoryRepository.deleteById(category.getId());
                    }
            );
            em.flush();

            List<ProductCategory> categories = new ArrayList<>();
            for (Long categoryId : request.getCategoryId()) {
                categories.add(ProductCategory.builder()
                        .product(product)
                        .category(categoryService.getCategory(categoryId))
                        .build());
            }
            createCategoryAssociation(request.getCategoryId(), product);
        }

        product.updateProduct(request.getName(),
                request.getPrice(),
                request.getDetails(),
                request.getActiveForSale()
        );

        logProduct(product);
    }

    @Transactional
    public void deleteProduct(DeleteProductRequest request) throws BadRequestException {
        Product product = productRepository.findByIdAndUseYn(request.getId(), true)
                .orElseThrow(BadRequestException::new);
        product.deleteProduct(request.getId());
        logProduct(product);
    }

    private List<CategoryResponse> getCategoryResponseList(Product product) {
        return product.getCategories().stream()
                .map(o -> CategoryResponse.builder()
                        .id(o.getCategory().getId())
                        .name(o.getCategory().getName())
                        .parentId(o.getCategory().getParent().getId())
                        .parentName(o.getCategory().getParent().getName())
                        .build())
                .collect(Collectors.toList());
    }

    private List<OptionsResponse> getOptionsResponses(Product product) {
        return product.getOptions().stream()
                .map(o -> OptionsResponse.builder()
                        .id(o.getId())
                        .name(o.getName())
                        .price(o.getPrice())
                        .build())
                .collect(Collectors.toList());
    }

    private void logProduct(Product product) {
        productLogRepository.save(ProductLog.builder()
                .product_id(product.getId())
                .name(product.getName())
                .price(product.getPrice())
                .details(product.getDetails())
                .compId(product.getCompId())
                .useYn(product.getUseYn())
                .activeForSale(product.getActiveForSale())
                .createdAt(product.getCreatedAt())
                .createdBy(product.getCreatedBy())
                .lastModifiedAt(product.getLastModifiedAt())
                .lastModifiedBy(product.getLastModifiedBy())
                .build());
    }

    private void createCategoryAssociation(List<Long> categoryId, Product product) throws BadRequestException {
        List<ProductCategory> productCategories = new ArrayList<>();
        for (Long id : categoryId) {
            productCategories.add(
                    ProductCategory.builder()
                            .product(product)
                            .category(categoryService.getCategory(id))
                            .build()
            );
        }
        productCategoryRepository.saveAll(productCategories);
        product.setCategory(productCategories);
    }

    public Page<ProductSearchResponse> searchProduct(SearchProductCondition condition) {
        PageRequest pageRequest = PageRequest.of(condition.getPageNumber(), condition.getPageSize());

        return productRepository.search(condition, pageRequest);
    }

}
