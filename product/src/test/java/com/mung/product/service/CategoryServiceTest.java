package com.mung.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.mung.product.domain.Category;
import com.mung.product.dto.CategoryDto.AddCategoryRequest;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@SpringBootTest
class CategoryServiceTest {

    @Autowired
    CategoryService categoryService;

    @Test
    public void 카테고리_등록_메인() throws Exception {
        // given
        AddCategoryRequest addCategoryRequest = AddCategoryRequest.builder()
            .name("main1")
            .build();

        // when
        Category category = categoryService.addCategory(addCategoryRequest);

        // then
        assertEquals("main1", category.getName());
        assertEquals(0, category.getDepth());
    }

    @Test
    public void 카테고리_등록_서브() throws Exception {
        // given
        AddCategoryRequest addCategoryRequest = AddCategoryRequest.builder()
            .parentId(1L)
            .name("sub2")
            .build();

        // when
        Category category = categoryService.addCategory(addCategoryRequest);

        // then
        assertEquals("sub2", category.getName());
        assertEquals(1, category.getDepth());
    }

    @Test
    public void 카테고리_등록_서브_실패_부모존재X() throws Exception {
        // given
        AddCategoryRequest addCategoryRequest = AddCategoryRequest.builder()
            .parentId(0L)
            .name("sub1")
            .build();

        // expected
        assertThrows(BadRequestException.class,
            () -> categoryService.addCategory(addCategoryRequest));
    }

}