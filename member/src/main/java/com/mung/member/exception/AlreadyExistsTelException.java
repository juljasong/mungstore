package com.mung.member.exception;

import com.mung.common.exception.CommonException;
import lombok.Getter;

@Getter
public class AlreadyExistsTelException extends CommonException {

    private static final String MESSAGE = "이미 가입된 휴대폰 번호 입니다.";

    public AlreadyExistsTelException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
