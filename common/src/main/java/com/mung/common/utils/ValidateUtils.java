package com.mung.common.utils;

import com.mung.common.domain.Validate;
import com.mung.common.domain.Validate.Regex;
import com.mung.common.exception.InvalidEmailException;
import com.mung.common.exception.InvalidPasswordException;
import com.mung.common.exception.InvalidZipcodeException;

public final class ValidateUtils {

    public static void validatePassword(String password) {
        if (!password.matches(Validate.Regex.VALID_PASSWORD)) {
            throw new InvalidPasswordException();
        }
    }
    public static void validateEmail(String email) {
        if (!email.matches(Regex.VALID_EMAIL)) {
            throw new InvalidEmailException();
        }
    }

    public static void validateZipcode(String zipcode) {
        if (!zipcode.matches(Regex.VALID_ZIPCODE)) {
            throw new InvalidZipcodeException();
        }
    }

}
