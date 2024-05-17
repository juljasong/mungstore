package com.mung.member.exception;

import com.mung.common.exception.CommonException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class AlreadyExistsTelException extends CommonException {

    private static final String MESSAGE = "이미 가입된 휴대폰 번호 입니다.";

    public AlreadyExistsTelException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
