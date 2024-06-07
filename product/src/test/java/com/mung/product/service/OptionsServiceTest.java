package com.mung.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mung.common.exception.DuplicateKeyException;
import com.mung.product.domain.Options;
import com.mung.product.dto.OptionsDto.AddOptionsRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class OptionsServiceTest {

    @Autowired
    OptionsService optionsService;

    @Test
    public void 옵션추가_성공() {
        // given
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
        AddOptionsRequest option = AddOptionsRequest.builder()
            .productId(1L)
            .name("dddd")
            .price(100)
            .available(true)
            .build();

        // when
        optionsService.addOptions(option);

        // then
        assertThrows(DuplicateKeyException.class,
            () -> optionsService.addOptions(option));
    }

}