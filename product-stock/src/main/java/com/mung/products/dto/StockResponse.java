package com.mung.products.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class StockResponse {
    Long productId;
    String productName;
    String productDetails;
    int productPrice;
    int totalPrice;
    Long categoryId;
    Long optionId;
    String optionName;
    int optionPrice;
    String skuId;
    int quantity;
}
