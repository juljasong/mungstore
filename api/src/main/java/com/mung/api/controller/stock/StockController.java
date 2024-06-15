package com.mung.api.controller.stock;

import com.mung.common.response.MessageResponse;
import com.mung.product.service.ProductService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class StockController {

    private final ProductService productService;

    @GetMapping("/stock/{productId}")
    public MessageResponse<?> getStockByProduct(@PathVariable Long productId) {
        return MessageResponse.builder()
            .data(productService.getStockByProduct(productId))
            .build();
    }

}
