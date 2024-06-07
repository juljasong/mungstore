package com.mung.api.controller.stock;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mung.api.controller.MockMember;
import com.mung.common.domain.Validate.Message;
import com.mung.member.domain.Role;
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
class StockControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    @Test
    @MockMember(id = 4L, name = "ADMIN", role = Role.ADMIN)
    public void 재고조회_성공() throws Exception {
        // given
        Long productId = 1L;

        // expected
        mockMvc.perform(get("/admin/stock/" + productId)
                .accept(APPLICATION_JSON)
            )
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
            .andExpect(jsonPath("$.data[0].productId").value(1))
            .andDo(print());
    }

    @Test
    @MockMember(id = 4L, name = "ADMIN", role = Role.ADMIN)
    public void 재고조회_실패_없는상품() throws Exception {
        // given
        Long productId = 123124L;

        // expected
        mockMvc.perform(get("/admin/stock/" + productId)
                .accept(APPLICATION_JSON)
            )
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.message").value(Message.BAD_REQUEST))
            .andDo(print());
    }


}