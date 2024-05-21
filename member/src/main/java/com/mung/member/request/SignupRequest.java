package com.mung.member.request;

import com.mung.common.domain.Validate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignupRequest {

    @NotBlank(message = Validate.MESSAGE.EMPTY_EMAIL)
    @Pattern(regexp = Validate.REGEX.VALID_EMAIL
            , message = Validate.MESSAGE.VALID_EMAIL)
    private String email;

    @NotBlank(message = Validate.MESSAGE.EMPTY_PASSWORD)
    @Pattern(regexp = Validate.REGEX.VALID_PASSWORD
            , message = Validate.MESSAGE.VALID_PASSWORD)
    private String password;

    @NotBlank(message = Validate.MESSAGE.EMPTY_NAME)
    private String name;

    @Size(min = 11, max = 11, message = Validate.MESSAGE.VALID_TEL)
    private String tel;

    private String zipcode;
    private String city;
    private String street;

    private String role;

    @Builder
    public SignupRequest(String email, String password, String name, String tel, String zipcode, String city, String street, String role) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.tel = tel;
        this.zipcode = zipcode;
        this.city = city;
        this.street = street;
        this.role = role;
    }
}
