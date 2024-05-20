package com.mung.api.controller.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mung.common.domain.ValidateMessage;
import com.mung.member.request.ResetPasswordRequest;
import com.mung.member.request.ResetPasswordEmailRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MemberController.class)
class MemberControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;

    //@Test
    @DisplayName("[P] 비밀번호 재설정 이메일을 보낸다.")
    public void 비밀번호_재설정_이메일_보내기_성공() throws Exception {
        // given
        ResetPasswordEmailRequest request = new ResetPasswordEmailRequest("z.kotzen@gmail.com", "01011111115");
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
    public void 메일건_에러() throws Exception {
        // given
        ResetPasswordEmailRequest request = new ResetPasswordEmailRequest("julja.song@gmail.com", "01047286836");
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
    public void 비밀번호_재설정_이메일_보내기_실패_이메일_휴대폰_불일치() throws Exception {
        // given
        ResetPasswordEmailRequest request = new ResetPasswordEmailRequest("z.kotzen@gmail.com", "01011111111");
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

    //@Test
    @DisplayName("[P] 비밀번호를 재설정한다.")
    public void 비밀번호_재설정_성공() throws Exception {
        // given
        String uuid = "4f8eb20b-f930-46ae-a911-fd6d9f47c497";
        ResetPasswordRequest request = new ResetPasswordRequest("Mung!mung1");
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
    public void 비밀번호_재설정_실패_무효uuid() throws Exception {
        // given
        String uuid = "incorrect";
        ResetPasswordRequest request = new ResetPasswordRequest("Mung!mung1");
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
    public void 비밀번호_재설정_실패_비밀번호_유효성_체크() throws Exception {
        // given
        String uuid = "a11c3a9c-7cef-4df1-a7e2-5de37b8d9408";
        ResetPasswordRequest request = ResetPasswordRequest.builder()
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