package com.mung.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;

import com.mung.common.exception.BadRequestException;
import com.mung.product.domain.Category;
import com.mung.product.domain.Product;
import com.mung.product.dto.ProductDto.AddProductRequest;
import com.mung.product.dto.ProductDto.DeleteProductRequest;
import com.mung.product.dto.ProductDto.UpdateProductRequest;
import com.mung.product.repository.ProductRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;


@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    ProductService productService;

    @Mock
    ProductRepository productRepository;
    @Mock
    CategoryService categoryService;

    @Test
    public void 상품등록_성공() {
        // given
        given(categoryService.getCategory(anyLong()))
            .willReturn(Optional.of(Category.builder().build()));
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
    public void 상품수정_성공() {
        // given
        given(categoryService.getCategory(anyLong()))
            .willReturn(Optional.of(Category.builder().build()));
        given(productRepository.findByIdAndUseYn(anyLong(), any()))
            .willReturn(Optional.of(Product.builder().build()));

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

    }

    @Test
    public void 상품수정_실패_존재하지않는카테고리() {
        // given
        given(categoryService.getCategory(anyLong()))
            .willReturn(Optional.empty());
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
    public void 상품수정_실패_존재하지않는상품() {
        // given
        given(categoryService.getCategory(anyLong()))
            .willReturn(Optional.of(Category.builder().build()));
        given(productRepository.findByIdAndUseYn(anyLong(), any()))
            .willReturn(Optional.empty());
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
    public void 상품삭제_성공() {
        // given
        Product product = Product.builder().build();
        ReflectionTestUtils.setField(product, "id", 1L);

        given(productRepository.findByIdAndUseYn(anyLong(), any()))
            .willReturn(Optional.of(product));
        DeleteProductRequest request = DeleteProductRequest.builder()
            .id(1L)
            .build();

        // when
        productService.deleteProduct(request);

        // then
        assertEquals(false, product.getUseYn());
    }

    @Test
    public void 상품삭제_실패_존재하지않는상품() {
        // given
        given(productRepository.findByIdAndUseYn(anyLong(), any()))
            .willReturn(Optional.empty());
        DeleteProductRequest request = DeleteProductRequest.builder()
            .id(10000L)
            .build();

        // expected
        assertThrows(BadRequestException.class,
            () -> productService.deleteProduct(request));

    }


}