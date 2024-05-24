package com.mung.product.repository;

import com.mung.product.domain.ProductLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductLogRepository extends JpaRepository<ProductLog, Long> {
}
