package com.mung.api.controller.member;

import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mung.api.controller.MockMember;
import com.mung.common.domain.Validate.Message;
import com.mung.member.config.JwtUtil;
import com.mung.member.domain.Cart;
import com.mung.member.domain.Role;
import com.mung.member.dto.CartDto.AddCartDto;
import com.mung.member.repository.CartRedisRepository;
import java.util.ArrayList;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpStatus;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class CartControllerTest {

    @MockBean
    JwtUtil jwtUtil;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Autowired
    CartRedisRepository cartRedisRepository;

    @Test
    @MockMember(id = 1L, name = "USER", role = Role.USER)
    public void 장바구니추가_성공() throws Exception {
        // given
        given(jwtUtil.getMemberId(anyString()))
            .willReturn(1L);

        List<AddCartDto> addCartRequestList = new ArrayList<>();
        addCartRequestList.add(AddCartDto.builder()
            .productId(1L)
            .optionId(1L)
            .count(1)
            .build());

        cartRedisRepository.save(Cart.builder()
            .memberId(1L)
            .cartList(addCartRequestList)
            .build());

        addCartRequestList.add(AddCartDto.builder()
            .productId(1L)
            .optionId(2L)
            .count(1)
            .build());

        String json = objectMapper.writeValueAsString(addCartRequestList);

        // expected
        mockMvc.perform(post("/cart")
                .header("Authorization", "Bearer test")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
            .andDo(print());

    }

    @Test
    @MockMember(id = 2L, name = "USER", role = Role.USER)
    public void 장바구니추가_장바구니존재x() throws Exception {
        // given
        given(jwtUtil.getMemberId(anyString()))
            .willReturn(2L);

        List<AddCartDto> addCartRequestList = new ArrayList<>();
        addCartRequestList.add(AddCartDto.builder()
            .productId(1L)
            .optionId(1L)
            .count(1)
            .build());

        String json = objectMapper.writeValueAsString(addCartRequestList);
        System.out.println("json = " + json);

        // expected
        mockMvc.perform(post("/cart")
                .header("Authorization", "Bearer test")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
            .andDo(print());
    }

    @Test
    @MockMember(id = 1L, name = "USER", role = Role.USER)
    public void 장바구니추가_실패_상품X() throws Exception {
        // given
        given(jwtUtil.getMemberId(anyString()))
            .willReturn(2L);

        List<AddCartDto> addCartRequestList = new ArrayList<>();
        addCartRequestList.add(AddCartDto.builder()
            .productId(11241123L)
            .optionId(1L)
            .count(1)
            .build());

        String json = objectMapper.writeValueAsString(addCartRequestList);
        System.out.println("json = " + json);

        // expected
        mockMvc.perform(post("/cart")
                .header("Authorization", "Bearer test")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(Message.BAD_REQUEST))
            .andDo(print());
    }

    @Test
    @MockMember(id = 1L, name = "USER", role = Role.USER)
    public void 장바구니추가_실패_옵션X() throws Exception {
        // given
        given(jwtUtil.getMemberId(anyString()))
            .willReturn(2L);

        List<AddCartDto> addCartRequestList = new ArrayList<>();
        addCartRequestList.add(AddCartDto.builder()
            .productId(1L)
            .optionId(223523645741L)
            .count(1)
            .build());

        String json = objectMapper.writeValueAsString(addCartRequestList);
        System.out.println("json = " + json);

        // expected
        mockMvc.perform(post("/cart")
                .header("Authorization", "Bearer test")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(Message.BAD_REQUEST))
            .andDo(print());
    }

    @Test
    @MockMember(id = 1L, name = "USER", role = Role.USER)
    public void 장바구니추가_실패_재고부족() throws Exception {
        // given
        given(jwtUtil.getMemberId(anyString()))
            .willReturn(1L);

        List<AddCartDto> addCartRequestList = new ArrayList<>();
        addCartRequestList.add(AddCartDto.builder()
            .productId(1L)
            .optionId(1L)
            .count(324284534)
            .build());

        String json = objectMapper.writeValueAsString(addCartRequestList);
        System.out.println("json = " + json);

        // expected
        mockMvc.perform(post("/cart")
                .header("Authorization", "Bearer test")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(Message.OUT_OF_STOCK))
            .andDo(print());
    }

}