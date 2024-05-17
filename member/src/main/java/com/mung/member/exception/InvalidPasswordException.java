package com.mung.member.exception;

import com.mung.common.exception.CommonException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class InvalidPasswordException extends CommonException {

    private static final String MESSAGE = "비밀번호는 영문자(대,소문자), 숫자, 특수문자를 포함하여 8-15자 이내로 작성 해야 합니다.";

    public InvalidPasswordException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
