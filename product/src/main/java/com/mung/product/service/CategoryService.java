package com.mung.product.service;

import com.mung.product.domain.Category;
import com.mung.product.repository.CategoryRepository;
import com.mung.product.request.AddCategoryRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    @Transactional
    public Category addCategory(AddCategoryRequest request) throws BadRequestException {
        Category parent = null;

        if (request.getParentId() != null) {
            parent = categoryRepository.findById(request.getParentId())
                    .orElseThrow(BadRequestException::new);
        }

        Category category = Category.builder()
                .name(request.getName())
                .build();

        if (request.getParentId() != null) {
            parent.addChildCategory(category);
        }

        categoryRepository.save(category);
        return category;
    }

    @Transactional
    public Category getCategory(Long categoryId) throws BadRequestException {
        return categoryRepository.findById(categoryId)
                .orElseThrow(BadRequestException::new);
    }

}
