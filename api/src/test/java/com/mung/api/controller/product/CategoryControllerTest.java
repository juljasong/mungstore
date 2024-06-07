package com.mung.api.controller.product;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mung.api.controller.MockMember;
import com.mung.member.domain.Role;
import com.mung.product.dto.CategoryDto.AddCategoryRequest;
import com.mung.product.repository.CategoryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class CategoryControllerTest {

    @Autowired
    CategoryRepository categoryRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @MockMember(id = 1L, name = "ADMIN", role = Role.ADMIN)
    public void 카테고리등록_성공() throws Exception {
        // given
        AddCategoryRequest request = AddCategoryRequest.builder()
            .parentId(1L)
            .name("category1-1")
            .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/admin/category")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
            .andDo(print());
    }

    @Test
    @MockMember(id = 1L, name = "ADMIN", role = Role.ADMIN)
    public void 카테고리등록_실패_부모카테고리X() throws Exception {
        // given
        AddCategoryRequest request = AddCategoryRequest.builder()
            .parentId(1000L)
            .name("category3-1")
            .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/admin/category")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
            .andDo(print());
    }

    @Test
    public void 전체카테고리조회_성공() throws Exception {
        mockMvc.perform(get("/category"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
            .andDo(print());
    }


}