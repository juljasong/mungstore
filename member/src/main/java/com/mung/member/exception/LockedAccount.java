package com.mung.member.exception;

import com.mung.common.exception.CommonException;
import lombok.Getter;

@Getter
public class LockedAccount extends CommonException {

    private static final String MESSAGE = "비밀번호를 5회 이상 잘못 입력하여 비밀번호가 초기화 되었습니다. 비밀번호를 재설정 해주십시오.";

    public LockedAccount() {
        super(MESSAGE);
    }

    @Override
    public int getStatusCode() {
        return 400;
    }
}
