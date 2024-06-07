package com.mung.product.repository;

import com.mung.product.dto.ProductDto.ProductSearchResponse;
import com.mung.product.dto.ProductDto.SearchProductCondition;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {

    Page<ProductSearchResponse> search(SearchProductCondition condition, Pageable pageable);
}
