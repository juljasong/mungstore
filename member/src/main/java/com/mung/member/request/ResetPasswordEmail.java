package com.mung.member.request;

import jakarta.validation.constraints.NotBlank;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResetPasswordEmail {

    @NotBlank(message = "이메일을 입력해 주세요.")
    private String email;
    @NotBlank(message = "휴대폰 번호를 입력해 주세요.")
    private String tel;

}