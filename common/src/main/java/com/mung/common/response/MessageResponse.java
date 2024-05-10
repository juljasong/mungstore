package com.mung.common.response;

import lombok.Builder;
import lombok.Getter;

/**
 * {
 *     "message" : "message"
 * }
 */
@Getter
public class MessageResponse {

    private final String message;

    @Builder
    public MessageResponse(String message) {
        this.message = message;
    }

}
