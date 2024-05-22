package com.mung.common.response;

import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;

/**
 * {
 *     "code" : code,
 *     "message" : "message",
 *     "data" : data
 * }
 */
@Getter
public class MessageResponse<T> {

    private final int code;
    private final String message;
    private final T data;

    @Builder
    public MessageResponse(int code, String message, T data) {
        this.code = (code == 0 ? HttpStatus.OK.value() : code);
        this.message = (!StringUtils.hasText(message) ? HttpStatus.OK.getReasonPhrase() : message);
        this.data = data;
    }

    public static MessageResponse ofSuccess() {
        HttpStatus statusCode = HttpStatus.OK;
        return MessageResponse.builder()
                .code(statusCode.value())
                .message(statusCode.getReasonPhrase())
                .data(null)
                .build();
    }

}
