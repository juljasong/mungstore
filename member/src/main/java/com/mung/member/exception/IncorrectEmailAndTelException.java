package com.mung.member.exception;

import com.mung.common.exception.CommonException;
import lombok.Getter;

@Getter
public class IncorrectEmailAndTelException extends CommonException {

    private static final String MESSAGE = "가입하신 이메일과 휴대폰 번호가 일치해야 합니다.";

    public IncorrectEmailAndTelException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
