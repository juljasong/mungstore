package com.mung.member.request;

import com.mung.common.domain.ValidateMessage;
import com.mung.common.domain.ValidateRegex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Login {

    @NotBlank(message = ValidateMessage.MESSAGE.EMPTY_EMAIL)
    @Pattern(regexp = ValidateRegex.REGEX.VALID_EMAIL
            , message = ValidateMessage.MESSAGE.VALID_EMAIL)
    private String email;

    @NotBlank(message = ValidateMessage.MESSAGE.EMPTY_PASSWORD)
    private String password;

    @Builder
    public Login(String email, String password) {
        this.email = email;
        this.password = password;
    }
}
