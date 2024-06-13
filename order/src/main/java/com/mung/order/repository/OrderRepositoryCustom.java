package com.mung.order.repository;

import com.mung.order.dto.OrderDto.GetOrdersResponse;
import com.mung.order.dto.OrderDto.OrderSearchRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OrderRepositoryCustom {

    Page<GetOrdersResponse> search(OrderSearchRequest condition, Pageable pageable);
}
