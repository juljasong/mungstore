package com.mung.api.controller.member;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mung.common.domain.Validate;
import com.mung.member.domain.Member;
import com.mung.member.domain.Role;
import com.mung.member.repository.MemberRepository;
import com.mung.member.request.LoginRequest;
import com.mung.member.request.RefreshTokenRequest;
import com.mung.member.request.SignupRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpStatus;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import static org.hamcrest.text.MatchesPattern.matchesPattern;
import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
    public void 회원가입_유저_성공() throws Exception {
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
    public void 회원가입_기업_성공() throws Exception {
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
    public void 회원가입_관리자_성공() throws Exception {
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
    public void 회원가입_실패_비밀번호_유효성() throws Exception {
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
                .andExpect(jsonPath("$.validation.password").value(Validate.MESSAGE.VALID_PASSWORD))
                .andDo(print());

        Member member = memberRepository.findByEmail("user100@gmail.com")
                .orElseGet(() -> null);
        assertNull(member);
    }

    @Test
    @DisplayName("[F] 회원가입 - 이메일 중복 회원")
    public void 회원가입_실패_이메일_중복() throws Exception {
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
    public void 회원가입_실패_휴대폰_중복() throws Exception {
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

    @Test
    @DisplayName("[P] 로그인")
    public void 로그인_성공() throws Exception {
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
                .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
                .andDo(print());
    }

    @Test
    @DisplayName("[F] 로그인")
    public void 로그인_실패_아이디_비밀번호_불일치() throws Exception {
        // given
        LoginRequest request = LoginRequest.builder()
                .email("z.kotzen@gmail.com")
                .password("Mung!mung2")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/auth/login")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("아이디 혹은 비밀번호를 확인해 주세요."))
                .andDo(print());
    }
    
    //@Test
    @DisplayName("[P] 토큰 갱신")
    public void 리프레시_토큰으로_액세스_토큰_갱신_성공() throws Exception {
        // given
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0b2tlbiIsImp0aSI6IjE1IiwiZXhwIjoxNzE2MjExNzk5LCJpYXQiOjE3MTYxOTAxOTl9.hwzTF6Wbchw3HtIJPi_-aC1kg6FzupxuUW2AZjVQB4s")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/auth/refresh")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value("OK"))
                .andExpect(jsonPath("$.data.memberId").value("15"))
                .andExpect(header().string("Authorization", matchesPattern("^Bearer .*")))
                .andDo(print());
    }

    @Test
    @DisplayName("[F] 토큰 갱신 실패_무효 리프레시 토큰")
    public void 리프레시_토큰으로_액세스_토큰_갱신_실패_무효토큰() throws Exception {
        // given
        RefreshTokenRequest request = RefreshTokenRequest.builder()
                .refreshToken("eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0b2tlbiIsImp0aSI6IjE1IiwiZXhwIjoxNzE2MTkyMzE3LCJpYXQiOjE3MTYxOTA1MTd9.JPrLzM4N3UzNqP-fhQU3eYcmrhTDER3o3E9YOSuK158")
                .build();

        String json = objectMapper.writeValueAsString(request);

        // expected
        mockMvc.perform(post("/auth/refresh")
                        .contentType(APPLICATION_JSON)
                        .content(json)
                )
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .andDo(print());
    }

    //@Test
    @DisplayName("[P] 로그아웃")
    @WithMockUser(username = "z.kotzen@gmail.com", roles = {"USER"})
    public void 로그아웃() throws Exception {

        mockMvc.perform(get("/auth/logout")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0b2tlbiIsImp0aSI6IjE1IiwiZXhwIjoxNzE2MTk0MDIxLCJpYXQiOjE3MTYxOTIyMjF9.BwswIbAlOLBnplaQ0go8ak-A8AovMPnXf1C7PiYqIJY")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.message").value(HttpStatus.OK.getReasonPhrase()))
                .andDo(print());

    }

    @Test
    @DisplayName("[F] 로그아웃")
    @WithMockUser(username = "z.kotzen@gmail.com", roles = {"USER"})
    public void 로그아웃_실패() throws Exception {

        mockMvc.perform(get("/auth/logout")
                        .contentType(APPLICATION_JSON)
                        .header("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJ0b2tlbiIsImp0aSI6IjE1IiwiZXhwIjoxNzE2MTkzNTE2LCJpYXQiOjE3MTYxOTE3MTZ9.knNasn3raooOwlBz7jFY3eHboDHQAND51wRZ0UOiTL0")
                )
                .andExpect(status().isBadRequest())
                .andDo(print());

    }

}