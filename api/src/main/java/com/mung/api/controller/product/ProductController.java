package com.mung.api.controller.product;

import com.mung.common.response.MessageResponse;
import com.mung.product.dto.ProductDto.AddProductRequest;
import com.mung.product.dto.ProductDto.DeleteProductRequest;
import com.mung.product.dto.ProductDto.ProductResponse;
import com.mung.product.dto.ProductDto.SearchProductCondition;
import com.mung.product.dto.ProductDto.UpdateProductRequest;
import com.mung.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @GetMapping("/product")
    public MessageResponse<?> getProduct(@RequestParam Long productId) {
        ProductResponse productResponse = productService.getProductResponse(productId);
        return MessageResponse.builder()
            .data(productResponse)
            .build();
    }

    @PostMapping("/products")
    public MessageResponse<?> searchProduct(@RequestBody SearchProductCondition condition) {
        return MessageResponse.builder()
            .data(productService.searchProduct(condition))
            .build();
    }

    @PostMapping("/admin/product")
    public MessageResponse<?> addProduct(@RequestBody @Valid AddProductRequest addProductRequest) {
        productService.addProduct(addProductRequest);

        return MessageResponse.ofSuccess();
    }

    @PatchMapping("/admin/product")
    public MessageResponse<?> updateProduct(
        @RequestBody @Valid UpdateProductRequest updateProductRequest) {
        productService.updateProduct(updateProductRequest);

        return MessageResponse.ofSuccess();
    }

    @DeleteMapping("/admin/product")
    public MessageResponse<?> deleteProduct(@RequestBody @Valid DeleteProductRequest request) {
        productService.deleteProduct(request);

        return MessageResponse.ofSuccess();
    }


}
