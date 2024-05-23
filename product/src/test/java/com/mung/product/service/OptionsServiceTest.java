package com.mung.product.service;

import com.mung.common.exception.DuplicateKeyException;
import com.mung.product.domain.Options;
import com.mung.product.request.AddOptionsRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@Transactional
@SpringBootTest
class OptionsServiceTest {

    @Autowired OptionsService optionsService;

    @Test
    public void 옵션추가_성공() throws Exception {
        // given
        AddOptionsRequest option = AddOptionsRequest.builder()
                .productId(4L)
                .name("testsetset")
                .price(100)
                .build();

        // when
        Options options = optionsService.addOptions(option);

        // then
        assertEquals(4L, options.getProduct().getId());
        assertEquals("testsetset", options.getName());
    }

    @Test
    public void 옵션추가_실패_중복() throws Exception {
        // given
        AddOptionsRequest option = AddOptionsRequest.builder()
                .productId(4L)
                .name("dddd")
                .price(100)
                .build();

        // when
        optionsService.addOptions(option);

        // then
        assertThrows(DuplicateKeyException.class,
                () -> optionsService.addOptions(option));
    }

}