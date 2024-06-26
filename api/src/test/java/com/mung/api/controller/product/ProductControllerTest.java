package com.mung.api.controller.product;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mung.api.controller.MockMember;
import com.mung.common.domain.Validate.Message;
import com.mung.member.domain.Role;
import com.mung.product.domain.Product;
import com.mung.product.repository.ProductRepository;
import com.mung.product.dto.ProductDto.AddProductRequest;
import com.mung.product.dto.ProductDto.UpdateProductRequest;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class ProductControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    ProductRepository productRepository;

    @Test
    @MockMember(id = 4L, name = "ADMIN", role = Role.ADMIN)
    public void 상품등록_성공() throws Exception {
        // given
        AddProductRequest request = AddProductRequest.builder()
                .name("상품")
                .details("상세")
                .price(3000)
                .categoryId(3L)
                .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/admin/product")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
                .andDo(print());

    }

    @Test
    @MockMember(id = 1L, name = "ADMIN", role = Role.ADMIN)
    public void 상품등록_실패_존재하지않는카테고리() throws Exception {
        // given
        AddProductRequest request = AddProductRequest.builder()
                .name("상품")
                .details("상세")
                .price(3000)
                .categoryId(100L)
                .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/admin/product")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Message.BAD_REQUEST))
                .andDo(print());
    }

    @Test
    @MockMember(id = 1L, name = "ADMIN", role = Role.ADMIN)
    public void 상품등록_실패() throws Exception {
        // given
        AddProductRequest request = AddProductRequest.builder()
                .name("상품")
                .details("상세")
                .price(3000)
                .categoryId(100L)
                .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/admin/product")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Message.BAD_REQUEST))
                .andDo(print());
    }

    @Test
    public void 상품조회_성공() throws Exception {
        // given
        Long productId = 1L;

        // expected
        mockMvc.perform(get("/product?productId=" + productId)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
                .andExpect(jsonPath("$.data.id").value(1))
                .andDo(print());
    }

    @Test
    public void 상품조회_실패_존재하지않는상품() throws Exception {
        // given
        Long productId = 100000L;

        // expected
        mockMvc.perform(get("/product?productId=" + productId)
                        .accept(APPLICATION_JSON)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(Message.BAD_REQUEST))
                .andDo(print());
    }

    @Test
    @MockMember(id = 1L, name = "ADMIN", role = Role.ADMIN)
    public void 상품수정_성공() throws Exception {
        // given
        UpdateProductRequest request = UpdateProductRequest.builder()
                .id(1L)
                .name("이름22")
                .price(30000)
                .details("띠테일22")
                .categoryId(1L)
                .activeForSale(true)
                .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(patch("/admin/product")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
                .andDo(print());

    }

    @Test
    @MockMember(id = 1L, name = "ADMIN", role = Role.ADMIN)
    public void 상품삭제_성공() throws Exception {
        // given
        UpdateProductRequest request = UpdateProductRequest.builder()
                .id(1L)
                .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(delete("/admin/product")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
                .andDo(print());

        Optional<Product> product = productRepository.findById(request.getId());
        assertEquals(false, product.get().getUseYn());
    }

}