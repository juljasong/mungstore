package com.mung.common.exception;

import com.mung.common.domain.Validate;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class NotExistMemberException extends CommonException {

    private static final String MESSAGE = Validate.Message.BAD_REQUEST;

    public NotExistMemberException() {
        super(MESSAGE);
    }

    public NotExistMemberException(String message) {
        super(message);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }

}
