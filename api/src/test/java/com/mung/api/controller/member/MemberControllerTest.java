package com.mung.api.controller.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mung.common.domain.ValidateMessage;
import com.mung.member.request.ResetPassword;
import com.mung.member.request.ResetPasswordEmail;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@AutoConfigureMockMvc
@SpringBootTest
class MemberControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    //@Test
    @DisplayName("[P] 비밀번호 재설정 이메일을 보낸다.")
    public void sendResetPasswordEmail() throws Exception {
        // given
        ResetPasswordEmail request = new ResetPasswordEmail("z.kotzen@gmail.com", "01011111115");
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/password")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());
    }

    @Test
    @DisplayName("[F] 메일건 에러")
    public void mailgunError() throws Exception {
        // given
        ResetPasswordEmail request = new ResetPasswordEmail("julja.song@gmail.com", "01047286836");
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/password")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.message").value("Sending email was failed."))
                .andDo(print());
    }

    @Test
    @DisplayName("[F] 비밀번호 재설정 이메일을 보낸다. - 이메일, 휴대폰 번호 불일치")
    public void sendResetPasswordEmailx() throws Exception {
        // given
        ResetPasswordEmail request = new ResetPasswordEmail("z.kotzen@gmail.com", "01011111111");
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/password")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("가입하신 이메일과 휴대폰 번호가 일치해야 합니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[P] 비밀번호를 재설정한다.")
    public void resetPassword() throws Exception {
        // given
        String uuid = "4f8eb20b-f930-46ae-a911-fd6d9f47c497";
        ResetPassword request = new ResetPassword("Mung!mung1");
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/password/" + uuid)
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());
    }

    @Test
    @DisplayName("[F]비밀번호를 재설정한다. - 무효 uuid")
    public void resetPasswordx1() throws Exception {
        // given
        String uuid = "incorrect";
        ResetPassword request = new ResetPassword("Mung!mung1");
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/password/" + uuid)
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andDo(print());
    }

    //@Test
    @DisplayName("[F]비밀번호를 재설정한다. - 비밀번호 유효성 체크")
    public void resetPasswordx2() throws Exception {
        // given
        String uuid = "a11c3a9c-7cef-4df1-a7e2-5de37b8d9408";
        ResetPassword request = ResetPassword.builder()
                .password("dd")
                .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/password/" + uuid)
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.password").value(ValidateMessage.MESSAGE.VALID_PASSWORD))
                .andDo(print());
    }

}