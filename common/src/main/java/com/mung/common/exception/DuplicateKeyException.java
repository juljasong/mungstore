package com.mung.common.exception;

import com.mung.common.domain.Validate;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class DuplicateKeyException extends CommonException {

    private static final String MESSAGE = Validate.MESSAGE.DUPLICATE_DATA;

    public DuplicateKeyException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.INTERNAL_SERVER_ERROR.value();
    }

}
