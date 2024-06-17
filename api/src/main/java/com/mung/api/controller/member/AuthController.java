package com.mung.api.controller.member;

import com.mung.common.response.MessageResponse;
import com.mung.member.dto.LoginDto;
import com.mung.member.request.LoginRequest;
import com.mung.member.request.RefreshTokenRequest;
import com.mung.member.request.SignupRequest;
import com.mung.member.response.LoginResponse;
import com.mung.member.service.AuthService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Slf4j
@RequiredArgsConstructor
@RequestMapping("/auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/signup")
    public MessageResponse<?> signup(@RequestBody @Valid SignupRequest signupRequest) {
        authService.signup(signupRequest);
        return MessageResponse.ofSuccess();
    }

    @PostMapping("/login")
    public MessageResponse<?> login(@RequestBody @Valid LoginRequest loginRequest,
        HttpServletResponse response) {

        LoginDto loginDto = authService.login(loginRequest);
        Cookie refreshToken = new Cookie("refresh-token", loginDto.getRefreshToken());
        refreshToken.setHttpOnly(true);
        refreshToken.setSecure(true);

        response.addHeader("Authorization", "Bearer " + loginDto.getAccessToken());
        response.addCookie(refreshToken);

        return MessageResponse.builder()
            .data(LoginResponse.builder()
                .memberId(loginDto.getMemberId())
                .build())
            .build();
    }

    @PostMapping("/refresh")
    public MessageResponse<?> refreshAccessToken(
        @RequestBody RefreshTokenRequest refreshTokenRequest, HttpServletResponse response) {

        LoginDto loginDto = authService.refreshAccessToken(refreshTokenRequest.getRefreshToken());
        response.addHeader("Authorization", "Bearer " + loginDto.getAccessToken());

        return MessageResponse.builder()
            .data(LoginResponse.builder()
                .memberId(loginDto.getMemberId())
                .build())
            .build();
    }

    @GetMapping("/logout")
    public MessageResponse<?> logout(HttpServletRequest request, HttpServletResponse response) {

        authService.logout(request.getHeader("Authorization"));
        response.setHeader("Authorization", null);

        return MessageResponse.ofSuccess();
    }

}
