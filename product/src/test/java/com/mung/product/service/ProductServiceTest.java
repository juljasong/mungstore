package com.mung.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mung.common.exception.BadRequestException;
import com.mung.product.domain.Product;
import com.mung.product.dto.ProductDto.AddProductRequest;
import com.mung.product.dto.ProductDto.DeleteProductRequest;
import com.mung.product.dto.ProductDto.UpdateProductRequest;
import com.mung.product.repository.CategoryRepository;
import com.mung.product.repository.ProductRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class ProductServiceTest {

    @Autowired
    ProductService productService;
    @Autowired
    ProductRepository productRepository;
    @Autowired
    CategoryRepository categoryRepository;

    @Test
    public void 상품등록_성공() throws Exception {

        // given
        AddProductRequest request = AddProductRequest.builder()
            .name("product1")
            .details("상세1")
            .compId(11L)
            .categoryId(1L)
            .build();

        // when
        productService.addProduct(request);

        // then

    }

    @Test
    public void 상품수정_성공() throws Exception {
        // given
        UpdateProductRequest request = UpdateProductRequest.builder()
            .id(1L)
            .price(300)
            .name("test")
            .details("details")
            .activeForSale(false)
            .categoryId(1L)
            .build();

        // when
        productService.updateProduct(request);

        // then
        Product product = productRepository.findById(1L).get();
        assertEquals("test", product.getName());
        assertEquals(false, product.getActiveForSale());
        assertEquals(1L, product.getCategory().getId());
    }

    @Test
    public void 상품수정_실패_존재하지않는카테고리() throws Exception {
        // given
        UpdateProductRequest request = UpdateProductRequest.builder()
            .id(1L)
            .price(300)
            .name("test")
            .details("details")
            .activeForSale(false)
            .categoryId(10000L)
            .build();

        // then
        assertThrows(BadRequestException.class,
            () -> productService.updateProduct(request));
    }

    @Test
    public void 상품수정_실패_존재하지않는상품() throws Exception {
        // given
        UpdateProductRequest request = UpdateProductRequest.builder()
            .id(10000000L)
            .price(300)
            .name("test")
            .details("details")
            .activeForSale(false)
            .categoryId(1L)
            .build();

        // then
        assertThrows(BadRequestException.class,
            () -> productService.updateProduct(request));
    }

    @Test
    public void 상품삭제_성공() throws Exception {
        // given
        DeleteProductRequest request = DeleteProductRequest.builder()
            .id(1L)
            .build();

        // when
        productService.deleteProduct(request);

        // then
        Product product = productRepository.findById(request.getId()).get();
        assertEquals(false, product.getUseYn());
    }

    @Test
    public void 상품삭제_실패_존재하지않는상품() throws Exception {
        // given
        DeleteProductRequest request = DeleteProductRequest.builder()
            .id(10000L)
            .build();

        // expected
        assertThrows(BadRequestException.class,
            () -> productService.deleteProduct(request));

    }


}