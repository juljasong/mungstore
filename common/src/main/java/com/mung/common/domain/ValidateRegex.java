package com.mung.common.domain;

public enum ValidateRegex {

    VALID_EMAIL(REGEX.VALID_EMAIL),
    VALID_PASSWORD(REGEX.VALID_PASSWORD)
    ;

    public static class REGEX {
        public static final String VALID_EMAIL = "^[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])*@[0-9a-zA-Z]([-_.]?[0-9a-zA-Z])+[.][a-zA-Z]{2,3}$";
        public static final String VALID_PASSWORD = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,15}$";
    }

    ValidateRegex(String validEmail) {}
}
