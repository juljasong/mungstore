package com.mung.api.controller.product;

import com.mung.common.response.MessageResponse;
import com.mung.product.dto.ProductDto.AddProductRequest;
import com.mung.product.dto.ProductDto.DeleteProductRequest;
import com.mung.product.dto.ProductDto.SearchProductCondition;
import com.mung.product.dto.ProductDto.UpdateProductRequest;
import com.mung.product.dto.ProductDto.ProductResponse;
import com.mung.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
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
    public MessageResponse<?> searchProduct(@RequestBody SearchProductCondition condition) throws BadRequestException {
        return MessageResponse.builder()
                .data(productService.searchProduct(condition))
                .build();
    }

    @PostMapping("/admin/product")
    public MessageResponse<?> addProduct(@RequestBody @Valid AddProductRequest addProductRequest) throws Exception {
        productService.addProduct(addProductRequest);

        return MessageResponse.ofSuccess();
    }

    @PatchMapping("/admin/product")
    public MessageResponse<?> updateProduct(@RequestBody @Valid UpdateProductRequest updateProductRequest) throws Exception {
        productService.updateProduct(updateProductRequest);

        return MessageResponse.ofSuccess();
    }

    @DeleteMapping("/admin/product")
    public MessageResponse<?> deleteProduct(@RequestBody @Valid DeleteProductRequest request) throws Exception {
        productService.deleteProduct(request);

        return MessageResponse.ofSuccess();
    }


}
