package com.mung.api.controller.member;

import com.mung.common.response.MessageResponse;
import com.mung.member.request.Login;
import com.mung.member.request.Signup;
import com.mung.member.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

@RestController
@Slf4j
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/auth/signup/{role}")
    public MessageResponse signup(@RequestBody @Valid Signup signup, @PathVariable("role") String role) {
        authService.signup(signup, role);
        return MessageResponse.ofSuccess();
    }

    @PostMapping("/auth/login")
    public MessageResponse login(@RequestBody @Valid Login login, HttpServletResponse response) {
        String accessToken = authService.login(login);
        response.addHeader("Authorization", "Bearer " + accessToken);
        return MessageResponse.builder()
                .message("ok")
                .build();
    }

    @GetMapping("/auth/logout")
    public MessageResponse logout(HttpServletRequest request, HttpServletResponse response) {
        authService.logout(request.getHeader("Authorization"));
        response.setHeader("Authorization", null);

        return MessageResponse.ofSuccess();
    }
}
