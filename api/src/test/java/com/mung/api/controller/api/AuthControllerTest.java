package com.mung.api.controller.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mung.common.domain.ValidateMessage;
import com.mung.member.domain.Member;
import com.mung.member.domain.Role;
import com.mung.member.repository.MemberRepository;
import com.mung.member.request.LoginRequest;
import com.mung.member.request.SignupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@Transactional
@AutoConfigureMockMvc
@SpringBootTest
class AuthControllerTest {

    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    MemberRepository memberRepository;

    @Test
    @DisplayName("[P] 회원가입 - user")
    //@Rollback(value = false)
    public void signup_user() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
                .email("user100@gmail.com")
                .password("Mung!mung1")
                .name("user100")
                .tel("01011111100")
                .role("user")
                .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/auth/signup" )
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());

        // then
        Member member = memberRepository.findByEmail(request.getEmail()).get();
        assertNotNull(member);
        assertEquals(Role.USER, member.getRole());
        assertEquals("user100@gmail.com", member.getEmail());
    }

    @Test
    @DisplayName("[P] 회원가입 - comp")
    public void signup_comp() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
                .email("comp100@gmail.com")
                .password("Mung!mung1")
                .name("comp100")
                .tel("01011111100")
                .role("comp")
                .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());

        // then
        Member member = memberRepository.findByEmail(request.getEmail()).get();
        assertNotNull(member);
        assertEquals(Role.COMP, member.getRole());
        assertEquals("comp100@gmail.com", member.getEmail());
    }

    @Test
    @DisplayName("[P] 회원가입 - admin")
    public void signup_admin() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
                .email("admin100@gmail.com")
                .password("Mung!mung1")
                .name("admin100")
                .tel("01011111100")
                .role("admin")
                .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"))
                .andDo(print());

        // then
        Member member = memberRepository.findByEmail(request.getEmail()).get();
        assertNotNull(member);
        assertEquals(Role.ADMIN, member.getRole());
        assertEquals("admin100@gmail.com", member.getEmail());
    }

    @Test
    @DisplayName("[F] 회원가입 - 비밀번호 유효성")
    public void signup_validate_password() throws Exception {
        // given
        SignupRequest request = SignupRequest.builder()
                .email("user100@gmail.com")
                .password("mung!mung1")
                .name("user100")
                .tel("01011111100")
                .role("user")
                .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.validation.password").value(ValidateMessage.MESSAGE.VALID_PASSWORD))
                .andDo(print());

        Member member = memberRepository.findByEmail("user100@gmail.com")
                .orElseGet(() -> null);
        assertNull(member);
    }

    @Test
    @DisplayName("[F] 회원가입 - 이메일 중복 회원")
    public void signup_duplicate_email() throws Exception {
        // given
        memberRepository.save(Member.builder()
                        .email("user100@gmail.com")
                        .password("Mung!mung1")
                        .name("user100")
                        .tel("01011111100")
                        .role(Role.USER)
                .build());

        SignupRequest request = SignupRequest.builder()
                .email("user100@gmail.com")
                .password("Mung!mung1")
                .name("user100")
                .tel("01011111101")
                .role("user")
                .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 가입된 이메일 입니다."))
                .andDo(print());
    }

    @Test
    @DisplayName("[F] 회원가입 - 휴대폰번호 중복 회원")
    public void signup_duplicate_tel() throws Exception {
        // given
        memberRepository.save(Member.builder()
                .email("user101@gmail.com")
                .password("Mung!mung1")
                .name("user100")
                .tel("01011111100")
                .role(Role.USER)
                .build());

        SignupRequest request = SignupRequest.builder()
                .email("user100@gmail.com")
                .password("Mung!mung1")
                .name("user100")
                .tel("01011111100")
                .role("user")
                .build();
        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/auth/signup")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("이미 가입된 휴대폰 번호 입니다."))
                .andDo(print());

        Member member = memberRepository.findByEmail("user100@gmail.com").orElseGet(() -> null);
        assertNull(member);
    }

    //@Test
    @DisplayName("[P] 로그인")
    public void login() throws Exception {
        // given
        LoginRequest request = LoginRequest.builder()
                .email("z.kotzen@gmail.com")
                .password("Mung!mung1")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(header().string("Authorization", matchesPattern("^Bearer .*")))
                .andExpect(jsonPath("$.message").value("ok"))
                .andDo(print());
    }

}