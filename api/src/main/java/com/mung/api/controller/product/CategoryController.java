package com.mung.api.controller.product;

import com.mung.common.response.MessageResponse;
import com.mung.product.request.AddCategoryRequest;
import com.mung.product.service.CategoryService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/admin")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping("/category")
    public MessageResponse<?> addCategory(
        @RequestBody @Valid AddCategoryRequest addCategoryRequest) {
        categoryService.addCategory(addCategoryRequest);
        return MessageResponse.ofSuccess();
    }

}
