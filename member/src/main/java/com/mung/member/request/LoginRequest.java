package com.mung.member.request;

import com.mung.common.domain.Validate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginRequest {

    @NotBlank(message = Validate.MESSAGE.EMPTY_EMAIL)
    @Pattern(regexp = Validate.REGEX.VALID_EMAIL
            , message = Validate.MESSAGE.VALID_EMAIL)
    private String email;

    @NotBlank(message = Validate.MESSAGE.EMPTY_PASSWORD)
    private String password;

    @Builder
    public LoginRequest(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
