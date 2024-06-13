package com.mung.member.request;

import com.mung.common.domain.Validate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class SignupRequest {

    @NotBlank(message = Validate.Message.EMPTY_EMAIL)
    @Pattern(regexp = Validate.Regex.VALID_EMAIL,
        message = Validate.Message.VALID_EMAIL)
    private String email;

    @NotBlank(message = Validate.Message.EMPTY_PASSWORD)
    @Pattern(regexp = Validate.Regex.VALID_PASSWORD,
        message = Validate.Message.VALID_PASSWORD)
    private String password;

    @NotBlank(message = Validate.Message.EMPTY_NAME)
    private String name;

    @Size(min = 11, max = 11, message = Validate.Message.VALID_TEL)
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
