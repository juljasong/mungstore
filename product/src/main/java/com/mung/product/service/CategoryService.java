package com.mung.product.service;

import com.mung.common.exception.BadRequestException;
import com.mung.product.domain.Category;
import com.mung.product.dto.CategoryDto.AddCategoryRequest;
import com.mung.product.dto.CategoryDto.CategoriesResponse;
import com.mung.product.repository.CategoryRepository;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Category addCategory(AddCategoryRequest request) {
        int depth = 0;

        if (request.getParentId() != null) {
            depth = categoryRepository.findById(request.getParentId())
                .orElseThrow(BadRequestException::new)
                .getDepth() + 1;
        }

        Category category = Category.builder()
            .name(request.getName())
            .depth(depth)
            .build();

        categoryRepository.save(category);
        return category;
    }

    public Category getCategory(Long categoryId)  {
        return categoryRepository.findById(categoryId)
            .orElseThrow(BadRequestException::new);
    }

    public List<CategoriesResponse> getAllCategoriesResponse() {
        return categoryRepository.findAllCategories().stream().map(CategoriesResponse::of)
            .collect(Collectors.toList());
    }

}
