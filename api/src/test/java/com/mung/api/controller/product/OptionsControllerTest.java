package com.mung.api.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mung.product.repository.OptionsRepository;
import com.mung.product.request.AddOptionsRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class OptionsControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    OptionsRepository optionsRepository;

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    public void 옵션등록_성공() throws Exception {
        // given
        AddOptionsRequest request = AddOptionsRequest.builder()
                .productId(1L)
                .name("옵션1-1")
                .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/admin/options" )
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    public void 옵션등록_실패_중복옵션() throws Exception {
        // given
        AddOptionsRequest request = AddOptionsRequest.builder()
                .productId(1L)
                .name("옵션1-1")
                .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/admin/options" )
                        .contentType(APPLICATION_JSON)
                        .content(json)
                );
        mockMvc.perform(post("/admin/options" )
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("중복 데이터 입니다."))
                .andDo(print());
    }

    @Test
    @WithMockUser(username = "admin@gmail.com", roles = {"ADMIN"})
    public void 옵션등록_실패_상품id에상품존재X() throws Exception {
        // given
        AddOptionsRequest request = AddOptionsRequest.builder()
                .productId(100L)
                .name("옵션100-1")
                .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/admin/options" )
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andDo(print());
    }

}