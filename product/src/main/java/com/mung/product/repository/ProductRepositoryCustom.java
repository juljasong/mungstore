package com.mung.product.repository;

import com.mung.product.request.SearchProductCondition;
import com.mung.product.response.ProductSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductRepositoryCustom {
    Page<ProductSearchResponse> search(SearchProductCondition condition, Pageable pageable);
}
