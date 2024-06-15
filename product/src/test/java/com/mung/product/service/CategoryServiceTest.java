package com.mung.product.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.given;

import com.mung.common.exception.BadRequestException;
import com.mung.product.domain.Category;
import com.mung.product.dto.CategoryDto.AddCategoryRequest;
import com.mung.product.repository.CategoryRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategoryServiceTest {

    @InjectMocks
    CategoryService categoryService;

    @Mock
    CategoryRepository categoryRepository;

    @Test
    public void 카테고리_등록_메인() {
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
        given(categoryRepository.findById(anyLong()))
            .willReturn(Optional.of(Category.builder()
                .depth(0)
                .build()));
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