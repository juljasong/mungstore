package com.mung.member.request;

import com.mung.common.domain.ValidateMessage;
import com.mung.common.domain.ValidateRegex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResetPasswordEmail {

    @NotBlank(message = ValidateMessage.MESSAGE.EMPTY_EMAIL)
    @Pattern(regexp = ValidateRegex.REGEX.VALID_EMAIL
            , message = ValidateMessage.MESSAGE.VALID_EMAIL)
    private String email;

    @NotBlank(message = ValidateMessage.MESSAGE.EMPTY_TEL)
    @Size(min = 11, max = 11, message = ValidateMessage.MESSAGE.VALID_TEL)
    private String tel;

    @Builder
    public ResetPasswordEmail(String email, String tel) {
        this.email = email;
        this.tel = tel;
    }
}
