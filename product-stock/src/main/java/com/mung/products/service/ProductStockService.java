package com.mung.products.service;

import com.mung.common.exception.BadRequestException;
import com.mung.products.dto.StockResponse;
import com.mung.products.repository.ProductStockRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class ProductStockService {

    private final ProductStockRepository productStockRepository;

    public List<StockResponse> getStockByProduct(Long productId) {
        List<StockResponse> stockByProduct = productStockRepository.getStockByProduct(productId);

        if (stockByProduct.isEmpty()) {
            throw new BadRequestException();
        }

        return stockByProduct;
    }

}
