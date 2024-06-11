package com.mung.product.repository;

import com.mung.product.domain.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<Product, Long>, ProductRepositoryCustom {

    Optional<Product> findByIdAndUseYn(Long productId, Boolean useYn);

    @Query("select p.id, p.name, p.price, p.activeForSale, p.useYn, "
        + "        o.id, o.name, o.price "
        + "   from Product p left join p.options o "
        + "  where p.id = :productId "
        + "    and o.id = :optionId")
    List<Object[]> findByIdAndOptionId(@Param("productId") Long productId, @Param("optionId") Long optionId);

}
