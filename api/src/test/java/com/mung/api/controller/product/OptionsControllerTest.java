package com.mung.api.controller.product;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mung.api.controller.MockMember;
import com.mung.common.domain.Validate.Message;
import com.mung.member.domain.Role;
import com.mung.product.dto.OptionsDto.AddOptionsRequest;
import com.mung.product.repository.OptionsRepository;
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
class OptionsControllerTest {

    @Autowired
    OptionsRepository optionsRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @MockMember(id = 1L, name = "ADMIN", role = Role.ADMIN)
    public void 옵션등록_성공() throws Exception {
        // given
        AddOptionsRequest request = AddOptionsRequest.builder()
            .productId(1L)
            .name("옵션1-1")
            .available(true)
            .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/admin/options")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
            .andDo(print());
    }

    @Test
    @MockMember(id = 1L, name = "ADMIN", role = Role.ADMIN)
    public void 옵션등록_실패_중복옵션() throws Exception {
        // given
        AddOptionsRequest request = AddOptionsRequest.builder()
            .productId(1L)
            .name("옵션1-1")
            .available(true)
            .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/admin/options")
            .contentType(APPLICATION_JSON)
            .content(json)
        );
        mockMvc.perform(post("/admin/options")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andExpect(status().isInternalServerError())
            .andExpect(jsonPath("$.message").value("중복 데이터 입니다."))
            .andDo(print());
    }

    @Test
    @MockMember(id = 1L, name = "ADMIN", role = Role.ADMIN)
    public void 옵션등록_실패_상품id에상품존재X() throws Exception {
        // given
        AddOptionsRequest request = AddOptionsRequest.builder()
            .productId(100L)
            .name("옵션100-1")
            .available(true)
            .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/admin/options")
                .contentType(APPLICATION_JSON)
                .content(json)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(Message.BAD_REQUEST))
            .andDo(print());
    }

}