package com.mung.api.exception;

import com.mung.common.exception.CommonException;
import org.springframework.http.HttpStatus;

public class Unauthorized extends CommonException {

    private static final String MESSAGE = "인증이 필요 합니다.";

    public Unauthorized() {
        super(MESSAGE);
    }

    public Unauthorized(Throwable cause) {
        super(MESSAGE, cause);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.UNAUTHORIZED.value();
    }

}
