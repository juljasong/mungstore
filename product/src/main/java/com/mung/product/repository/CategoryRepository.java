package com.mung.product.repository;

import com.mung.product.domain.Category;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("select c from Category c where c.parent is null")
    List<Category> findAllCategories();

}
