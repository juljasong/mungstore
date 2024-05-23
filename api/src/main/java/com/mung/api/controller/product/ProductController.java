package com.mung.api.controller.product;

import com.mung.common.response.MessageResponse;
import com.mung.product.domain.Product;
import com.mung.product.request.AddProductRequest;
import com.mung.product.service.ProductService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class ProductController {

    private final ProductService productService;

    @PostMapping("/product")
    public MessageResponse<?> addProduct(@RequestBody @Valid AddProductRequest addProductRequest) throws BadRequestException {
        Product product = productService.addProduct(addProductRequest);

        return MessageResponse.builder()
                .data(product)
                .build();
    }
}
