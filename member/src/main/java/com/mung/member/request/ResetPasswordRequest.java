package com.mung.member.request;

import com.mung.common.domain.Validate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResetPasswordRequest {

    @NotBlank(message = Validate.Message.EMPTY_PASSWORD)
    @Pattern(regexp = Validate.Regex.VALID_PASSWORD
            , message = Validate.Message.VALID_PASSWORD)
    private String password;

    @Builder
    public ResetPasswordRequest(String password) {
        this.password = password;
    }
}
