package com.mung.member.exception;

import com.mung.common.domain.Validate;
import com.mung.common.exception.CommonException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidPasswordException extends CommonException {

    private static final String MESSAGE = Validate.Message.VALID_PASSWORD;

    public InvalidPasswordException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
