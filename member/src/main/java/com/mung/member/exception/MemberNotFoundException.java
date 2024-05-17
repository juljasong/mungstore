package com.mung.member.exception;

import com.mung.common.exception.CommonException;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
public class MemberNotFoundException extends CommonException {

    private static final String MESSAGE = "아이디 혹은 비밀번호를 확인해 주세요.";

    public MemberNotFoundException() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return HttpStatus.BAD_REQUEST.value();
    }
}
