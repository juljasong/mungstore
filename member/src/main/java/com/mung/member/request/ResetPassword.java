package com.mung.member.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResetPassword {

    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String password;

}
