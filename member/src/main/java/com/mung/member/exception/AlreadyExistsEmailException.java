package com.mung.member.exception;

import com.mung.common.exception.CommonException;
import lombok.Getter;

@Getter
public class AlreadyExistsEmailException extends CommonException {

    private static final String MESSAGE = "이미 가입된 이메일 입니다.";

    public AlreadyExistsEmailException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
