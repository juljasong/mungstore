package com.mung.member.request;

import com.mung.common.domain.ValidateMessage;
import com.mung.common.domain.ValidateRegex;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResetPasswordRequest {

    @NotBlank(message = ValidateMessage.MESSAGE.EMPTY_PASSWORD)
    @Pattern(regexp = ValidateRegex.REGEX.VALID_PASSWORD
            , message = ValidateMessage.MESSAGE.VALID_PASSWORD)
    private String password;

    @Builder
    public ResetPasswordRequest(String password) {
        this.password = password;
    }
}
