package com.mung.stock.service;

import com.mung.common.exception.BadRequestException;
import com.mung.stock.domain.Stock;
import com.mung.stock.repository.StockRepository;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;

    public boolean isStockAvailable(Long optionId, int count) {
        Stock stock = stockRepository.findByOptionId(optionId)
            .orElseThrow(BadRequestException::new);
        return (stock.getQuantity() - count) > 0;
    }

    public Optional<Stock> getStock(Long optionId) {
        return stockRepository.findByOptionId(optionId);
    }
}
