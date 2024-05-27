package com.mung.product.service;

import com.mung.product.domain.Product;
import com.mung.product.repository.CategoryRepository;
import com.mung.product.repository.ProductLogRepository;
import com.mung.product.repository.ProductRepository;
import com.mung.product.request.AddProductRequest;
import com.mung.product.request.DeleteProductRequest;
import com.mung.product.request.UpdateProductRequest;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@Transactional
@SpringBootTest
class ProductServiceTest {

    @Autowired ProductService productService;
    @Autowired ProductRepository productRepository;
    @Autowired CategoryRepository categoryRepository;

    @MockBean ProductLogRepository productLogRepository;

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

    @Test
    public void 상품수정_성공() throws Exception {
        // given
        UpdateProductRequest request = UpdateProductRequest.builder()
                .id(1L)
                .price(300)
                .name("test")
                .details("details")
                .activeForSale(false)
                .categoryId(List.of(1L, 2L, 3L))
                .build();

        // when
        productService.updateProduct(request);

        // then
        Product product = productRepository.findById(1L).get();
        assertEquals("test", product.getName());
        assertEquals(3, product.getCategories().size());
        assertEquals(false, product.getActiveForSale());
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
                .categoryId(List.of(100L))
                .build();

        // then
        assertThrows(BadRequestException.class,
                () ->productService.updateProduct(request));
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
                .categoryId(List.of(1L))
                .build();


        // then
        assertThrows(BadRequestException.class,
                () ->productService.updateProduct(request));
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
        verify(productLogRepository).save(any());
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