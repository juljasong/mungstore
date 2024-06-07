package com.mung.common.domain;

public enum Validate {

    NOT_EXIST_MEMBER(Message.NOT_EXIST_MEMBER),
    BAD_REQUEST(Message.BAD_REQUEST),
    NOT_EMPTY(Message.NOT_EMPTY),
    DUPLICATE_DATA(Message.DUPLICATE_DATA),
    EMPTY_EMAIL(Message.EMPTY_EMAIL),
    VALID_EMAIL(Message.VALID_EMAIL, Regex.VALID_EMAIL),
    EMPTY_PASSWORD(Message.EMPTY_PASSWORD),
    VALID_PASSWORD(Message.VALID_PASSWORD, Regex.VALID_PASSWORD),
    EMPTY_TEL(Message.EMPTY_TEL),
    VALID_TEL(Message.VALID_TEL),
    EMPTY_NAME(Message.EMPTY_NAME),
    VALID_ZIPCODE(Message.VALID_ZIPCODE);

    Validate(String message) {
    }

    Validate(String message, String regex) {
    }

    public static class Message {

        public static final String NOT_EXIST_MEMBER = "존재하지 않는 회원입니다.";
        public static final String BAD_REQUEST = "잘못된 요청입니다.";
        public static final String NOT_EMPTY = "필수 입력 항목을 확인해 주십시오.";
        public static final String DUPLICATE_DATA = "중복 데이터 입니다.";
        public static final String EMPTY_EMAIL = "이메일을 입력해 주십시오";
        public static final String VALID_EMAIL = "이메일 주소 양식을 확인해 주십시오.";
        public static final String EMPTY_PASSWORD = "비밀번호를 입력해 주십시오.";
        public static final String VALID_PASSWORD = "비밀번호는 영문자(대,소문자), 숫자, 특수문자를 포함하여 8-15자 이내로 입력해 주십시오.";
        public static final String EMPTY_TEL = "휴대폰 번호를 입력해 주십시오.";
        public static final String VALID_TEL = "11자리의 휴대폰 번호를 입력해 주십시오.";
        public static final String EMPTY_NAME = "이름을 입력해 주십시오.";
        public static final String VALID_ZIPCODE = "우편번호는 숫자 5자리로 입력해 주십시오.";
    }

    public static class Regex {

        public static final String VALID_EMAIL = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,3}$";
        public static final String VALID_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$";
    }

}
