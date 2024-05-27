package com.mung.api.controller.product;

import com.mung.common.response.MessageResponse;
import com.mung.product.domain.Product;
import com.mung.product.request.AddProductRequest;
import com.mung.product.request.DeleteProductRequest;
import com.mung.product.request.SearchProductCondition;
import com.mung.product.request.UpdateProductRequest;
import com.mung.product.response.ProductResponse;
import com.mung.product.response.ProductSearchResponse;
import com.mung.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/product")
    public MessageResponse<?> getProduct(@RequestParam Long productId) throws BadRequestException {
        ProductResponse productResponse = productService.getProductResponse(productId);
        return MessageResponse.builder()
                .data(productResponse)
                .build();
    }

    @PostMapping("/products")
    public Page<ProductSearchResponse> searchProduct(@RequestBody SearchProductCondition condition) throws BadRequestException {
        return productService.searchProduct(condition);
    }

    @PostMapping("/admin/product")
    public MessageResponse<?> addProduct(@RequestBody @Valid AddProductRequest addProductRequest) throws BadRequestException {
        Product product = productService.addProduct(addProductRequest);

        return MessageResponse.builder()
                .data(product)
                .build();
    }

    @PatchMapping("/admin/product")
    public MessageResponse<?> updateProduct(@RequestBody @Valid UpdateProductRequest updateProductRequest) throws BadRequestException {
        productService.updateProduct(updateProductRequest);

        return MessageResponse.ofSuccess();
    }

    @DeleteMapping("/admin/product")
    public MessageResponse<?> deleteProduct(@RequestBody @Valid DeleteProductRequest request) throws BadRequestException {
        productService.deleteProduct(request);

        return MessageResponse.ofSuccess();
    }


}
