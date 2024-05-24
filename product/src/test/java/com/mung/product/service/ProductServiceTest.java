package com.mung.product.service;

import com.mung.product.domain.Product;
import com.mung.product.repository.CategoryRepository;
import com.mung.product.request.AddProductRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class ProductServiceTest {

    @Autowired ProductService productService;

    @Autowired CategoryRepository categoryRepository;

    @Test
    public void 상품등록_성공() throws Exception {

        // given
        AddProductRequest request = AddProductRequest.builder()
                .name("product1")
                .details("상세1")
                .compId(11L)
                .categoryId(List.of(7L, 8L))
                .build();

        // when
        Product product = productService.addProduct(request);

        // then
        assertEquals("product1", product.getName());
        assertEquals("상세1", product.getDetails());
    }


}