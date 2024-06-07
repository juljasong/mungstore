package com.mung.member.service;

import com.mung.common.exception.BadRequestException;
import com.mung.member.config.JwtUtil;
import com.mung.member.domain.*;
import com.mung.member.dto.LoginDto;
import com.mung.member.exception.AlreadyExistsEmailException;
import com.mung.member.exception.AlreadyExistsTelException;
import com.mung.member.exception.MemberNotFoundException;
import com.mung.member.exception.Unauthorized;
import com.mung.member.repository.*;
import com.mung.member.request.LoginRequest;
import com.mung.member.request.SignupRequest;
import jakarta.persistence.EntityManager;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.AdditionalAnswers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock private MemberRepository memberRepository;
    @Mock private LoginLogRepository loginLogRepository;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private JwtUtil jwtUtil;
    @Mock private AccessTokenRedisRepository accessTokenRedisRepository;
    @Mock private RefreshTokenRedisRepository refreshTokenRedisRepository;
    @Mock private EntityManager em;

    @InjectMocks private AuthService authService;

    @Test
    public void 회원가입_성공() throws Exception {
        // given
        SignupRequest signupRequest = SignupRequest.builder()
                .email("test@gmail.com")
                .password("test!Test1")
                .name("테스트")
                .role("user")
                .build();

        Member member = Member.builder().address(new Address("", "", "")).build();
        ReflectionTestUtils.setField(member, "id", 0L);

        when(memberRepository.save(any(Member.class)))
                .then(AdditionalAnswers.returnsFirstArg());

        // when
        authService.signup(signupRequest);

        // then
        verify(memberRepository).save(any());
    }

    @Test
    public void 회원가입_실패_중복이메일() throws Exception {
        // given
        SignupRequest signupRequest = SignupRequest.builder()
                .email("test@gmail.com")
                .password("test!Test1")
                .name("테스트")
                .role("user")
                .build();
        Member member = Member.builder().build();

        given(memberRepository.findByEmail(signupRequest.getEmail()))
                .willReturn(Optional.ofNullable(member));

        // expected
        assertThrows(AlreadyExistsEmailException.class,
                () -> authService.signup(signupRequest));
    }

    @Test
    public void 회원가입_실패_중복휴대폰() throws Exception {
        // given
        SignupRequest signupRequest = SignupRequest.builder()
                .email("test@gmail.com")
                .password("test!Test1")
                .name("테스트")
                .role("user")
                .build();
        Member member = Member.builder().build();

        given(memberRepository.findByTel(signupRequest.getTel()))
                .willReturn(Optional.ofNullable(member));

        // expected
        assertThrows(AlreadyExistsTelException.class,
                () -> authService.signup(signupRequest));
    }

    @Test
    public void 로그인_성공() throws Exception {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@gmail.com")
                .password("test")
                .build();

        Member member = new Member("", "", "", null, Role.USER, null);
        ReflectionTestUtils.setField(member, "id", 15L);

        given(memberRepository.findByEmail(loginRequest.getEmail()))
                .willReturn(Optional.of(member));

        given(passwordEncoder.matches(any(), any()))
                .willReturn(true);

        given(jwtUtil.createToken(15L, JwtUtil.ACCESS_EXPIRATION_TIME))
                .willReturn("accessToken");
        given(jwtUtil.createToken(15L, JwtUtil.REFRESH_EXPIRATION_TIME))
                .willReturn("refreshToken");

        // when
        LoginDto login = authService.login(loginRequest);

        // then
        verify(loginLogRepository).save(any());
        assertEquals(15L, login.getMemberId());
        assertEquals("accessToken", login.getAccessToken());
        assertEquals("refreshToken", login.getRefreshToken());
    }

    @Test
    public void 로그인_실패_비밀번호불일치() throws Exception {
        // given
        LoginRequest loginRequest = LoginRequest.builder()
                .email("test@gmail.com")
                .password("test")
                .build();

        Member member = new Member("", "", "", null, Role.USER, null);
        ReflectionTestUtils.setField(member, "id", 15L);

        given(memberRepository.findByEmail(loginRequest.getEmail()))
                .willReturn(Optional.of(member));

        given(passwordEncoder.matches(any(), any()))
                .willReturn(false);

        // expected
        assertThrows(MemberNotFoundException.class,
                () -> authService.login(loginRequest));
        verify(loginLogRepository).save(any());
    }

    @Test
    public void 로그아웃_성공() throws Exception {
        // given
        String authorization = "Bearer abc";

        // when
        authService.logout(authorization);

        // then
        verify(jwtUtil).clearAccessAndRefreshToken(any());
    }

    @Test
    public void 리프레시토큰재발행_성공() throws Exception {
        // given
        String refreshToken = "refreshToken";

        given(jwtUtil.checkAndGetRefreshToken(refreshToken))
                .willReturn(Optional.ofNullable(RefreshToken.builder()
                        .memberId(15L)
                        .refreshToken("abc")
                        .build()));

        given(jwtUtil.createToken(15L, JwtUtil.ACCESS_EXPIRATION_TIME))
                .willReturn("accessToken");

        // when
        LoginDto loginDto = authService.refreshAccessToken(refreshToken);

        // then
        assertEquals("accessToken", loginDto.getAccessToken());
        assertEquals(15L, loginDto.getMemberId());
    }

    @Test
    public void 리프레시토큰재발행_실패_없는토큰() throws Exception {
        // given
        String refreshToken = "refreshToken";

        given(jwtUtil.checkAndGetRefreshToken(refreshToken))
                .willThrow(Unauthorized.class);

        // expected
        assertThrows(Unauthorized.class,
                () -> authService.refreshAccessToken(refreshToken));
    }

    @Test
    public void 리프레시토큰재발행_실패_무효jwt() throws Exception {
        // given
        String refreshToken = "refreshToken";

        given(jwtUtil.checkAndGetRefreshToken(refreshToken))
                .willThrow(BadRequestException.class);

        // expected
        assertThrows(BadRequestException.class,
                () -> authService.refreshAccessToken(refreshToken));
    }


}