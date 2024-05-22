package com.mung.member.request;

import com.mung.common.domain.Validate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResetPasswordEmailRequest {

    @NotBlank(message = Validate.MESSAGE.EMPTY_EMAIL)
    @Pattern(regexp = Validate.REGEX.VALID_EMAIL
            , message = Validate.MESSAGE.VALID_EMAIL)
    private String email;

    @NotBlank(message = Validate.MESSAGE.EMPTY_TEL)
    @Size(min = 11, max = 11, message = Validate.MESSAGE.VALID_TEL)
    private String tel;

    @Builder
    public ResetPasswordEmailRequest(String email, String tel) {
        this.email = email;
        this.tel = tel;
    }
}
