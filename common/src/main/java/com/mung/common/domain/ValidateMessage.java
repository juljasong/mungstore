package com.mung.common.domain;

public enum ValidateMessage {

    EMPTY_EMAIL(MESSAGE.EMPTY_EMAIL),
    VALID_EMAIL(MESSAGE.VALID_EMAIL),
    EMPTY_PASSWORD(MESSAGE.EMPTY_PASSWORD),
    VALID_PASSWORD(MESSAGE.VALID_PASSWORD),
    EMPTY_TEL(MESSAGE.EMPTY_TEL),
    VALID_TEL(MESSAGE.VALID_TEL),
    EMPTY_NAME(MESSAGE.EMPTY_NAME)
    ;

    public static class MESSAGE {
        public static final String EMPTY_EMAIL = "이메일을 입력해 주십시오";
        public static final String VALID_EMAIL = "이메일 주소 양식을 확인해 주십시오.";
        public static final String EMPTY_PASSWORD = "비밀번호를 입력해 주십시오.";
        public static final String VALID_PASSWORD = "비밀번호는 영문자(대,소문자), 숫자, 특수문자를 포함하여 8-15자 이내로 입력해 주십시오.";
        public static final String EMPTY_TEL = "휴대폰 번호를 입력해 주십시오.";
        public static final String VALID_TEL = "11자리의 휴대폰 번호를 입력해 주십시오.";
        public static final String EMPTY_NAME = "이름을 입력해 주십시오.";
    }

    ValidateMessage(String emptyTel) {}

}
