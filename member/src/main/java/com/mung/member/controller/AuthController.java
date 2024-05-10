package com.mung.member.controller;

import com.mung.common.response.MessageResponse;
import com.mung.member.request.Signup;
import com.mung.member.service.AuthService;
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
        return MessageResponse.builder()
                .message(authService.signup(signup, role))
                .build();
    }

}
