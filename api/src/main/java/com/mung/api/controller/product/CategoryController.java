package com.mung.api.controller.product;

import com.mung.common.response.MessageResponse;
import com.mung.product.dto.CategoryDto.AddCategoryRequest;
import com.mung.product.dto.CategoryDto.CategoriesResponse;
import com.mung.product.service.CategoryService;
import jakarta.validation.Valid;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.coyote.BadRequestException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping("/category")
    public MessageResponse<?> getCategory() throws BadRequestException {
        List<CategoriesResponse> response = categoryService.getAllCategoriesResponse();

        return MessageResponse.builder()
            .data(response)
            .build();
    }

    @PostMapping("/admin/category")
    public MessageResponse<?> addCategory(@RequestBody @Valid AddCategoryRequest addCategoryRequest)
        throws BadRequestException {
        categoryService.addCategory(addCategoryRequest);

        return MessageResponse.ofSuccess();
    }

}
