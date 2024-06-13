package com.mung.common.exception;

import com.mung.common.domain.Validate.Message;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidEmailException extends CommonException {

    private static final String MESSAGE = Message.VALID_EMAIL;

    public InvalidEmailException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
