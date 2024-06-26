package com.mung.api.config.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mung.common.response.ErrorResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;

@Slf4j
@RequiredArgsConstructor
public class Http403Handler implements AccessDeniedHandler {

    private final ObjectMapper objectMapper;

    @Override
    public void handle(HttpServletRequest request,
        HttpServletResponse response,
        AccessDeniedException accessDeniedException) throws IOException {

        log.error("[인증오류] 접근할 수 없습니다.");

        ErrorResponse errorResponse = ErrorResponse.builder()
            .code(HttpStatus.FORBIDDEN.value())
            .message("접근할 수 없습니다.")
            .build();

        response.setCharacterEncoding(StandardCharsets.UTF_8.displayName());
        response.setStatus(HttpServletResponse.SC_FORBIDDEN);
        
        objectMapper.writeValue(response.getWriter(), errorResponse);
    }
}
