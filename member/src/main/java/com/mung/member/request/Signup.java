package com.mung.member.request;

import jakarta.validation.constraints.NotBlank;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Signup {

    @NotBlank(message = "이메일을 입력해 주세요.")
    private String email;
    @NotBlank(message = "비밀번호를 입력해 주세요.")
    private String password;
    @NotBlank(message = "성함을 입력해 주세요.")
    private String name;

    private String tel;
    private String zipcode;
    private String city;
    private String street;

}
