package com.mung.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.*;

import com.mung.common.exception.DuplicateKeyException;
import com.mung.product.domain.Options;
import com.mung.product.domain.Product;
import com.mung.product.dto.OptionsDto.AddOptionsRequest;
import com.mung.product.repository.OptionsRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.util.ReflectionTestUtils;


@ExtendWith(MockitoExtension.class)
class OptionsServiceTest {

    @InjectMocks
    OptionsService optionsService;

    @Mock
    OptionsRepository optionsRepository;
    @Mock
    ProductService productService;

    @Test
    public void 옵션추가_성공() {
        // given
        Product product = Product.builder().build();
        ReflectionTestUtils.setField(product, "id", 1L);
        given(productService.getProduct(anyLong()))
            .willReturn(Optional.of(product));

        AddOptionsRequest option = AddOptionsRequest.builder()
            .productId(1L)
            .name("testsetset")
            .price(100)
            .available(true)
            .build();

        // when
        Options options = optionsService.addOptions(option);

        // then
        assertEquals(1L, options.getProduct().getId());
        assertEquals("testsetset", options.getName());
    }

    @Test
    public void 옵션추가_실패_중복() {
        // given
        given(productService.getProduct(anyLong()))
            .willReturn(Optional.of(Product.builder().build()));
        given(optionsRepository.save(any()))
            .willThrow(new DataIntegrityViolationException(""));

        AddOptionsRequest option = AddOptionsRequest.builder()
            .productId(1L)
            .name("dddd")
            .price(100)
            .available(true)
            .build();

        // expected
        assertThrows(DuplicateKeyException.class,
            () -> optionsService.addOptions(option));
    }

}