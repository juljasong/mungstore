package com.mung.products.repository;

import com.mung.product.domain.Product;
import com.mung.products.dto.StockResponse;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductStockRepository extends JpaRepository<Product, Long> {

    @Query(
        "select new com.mung.products.dto.StockResponse("
            + " p.id, p.name, p.details, p.price as productPrice, (p.price + o.price) as totalPrice, p.category.id, "
            + " o.id, o.name, o.price as optionPrice, "
            + " s.skuId, s.quantity) "
            + " from Product p left join p.options o "
            + " left join Stock s on s.optionId = o.id "
            + " where p.id = :productId")
    List<StockResponse> getStockByProduct(@Param("productId") Long productId);
}
