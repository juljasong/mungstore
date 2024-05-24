package com.mung.product.service;

import com.mung.product.domain.Product;
import com.mung.product.domain.ProductCategory;
import com.mung.product.domain.ProductCategoryPK;
import com.mung.product.domain.ProductLog;
import com.mung.product.repository.ProductCategoryRepository;
import com.mung.product.repository.ProductLogRepository;
import com.mung.product.repository.ProductRepository;
import com.mung.product.request.AddProductRequest;
import com.mung.product.response.CategoryResponse;
import com.mung.product.response.OptionsResponse;
import com.mung.product.response.ProductResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductLogRepository productLogRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final CategoryService categoryService;

    @Transactional
    public Product addProduct(AddProductRequest request) throws BadRequestException {
        Product product = Product.builder()
                .name(request.getName())
                .details(request.getDetails())
                .compId(request.getCompId())
                .activeForSale(true)
                .build();
        productRepository.save(product);

        createCategoryAssociation(request, product);
        logProduct(product);

        return product;
    }

    public Product getProduct(Long productId) throws BadRequestException {
        return  productRepository.findById(productId)
                .orElseThrow(BadRequestException::new);
    }

    public ProductResponse getProductResponse(Long productId) throws BadRequestException {
        Product product = productRepository.findById(productId)
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

    private List<CategoryResponse> getCategoryResponseList(Product product) {
        return product.getCategories().stream()
                .map(o -> CategoryResponse.builder()
                        .id(o.getId().getCategory().getId())
                        .name(o.getId().getCategory().getName())
                        .parentId(o.getId().getCategory().getParent().getId())
                        .parentName(o.getId().getCategory().getParent().getName())
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
                .details(product.getDetails())
                .compId(product.getCompId())
                .activeForSale(product.getActiveForSale())
                .build());
    }

    private void createCategoryAssociation(AddProductRequest request, Product product) throws BadRequestException {
        List<ProductCategory> productCategories = new ArrayList<>();
        for (Long categoryId : request.getCategoryId()) {
            productCategories.add(
                    new ProductCategory(
                        new ProductCategoryPK(product, categoryService.getCategory(categoryId))
                    )
            );
        }

        productCategoryRepository.saveAll(productCategories);
    }


}
