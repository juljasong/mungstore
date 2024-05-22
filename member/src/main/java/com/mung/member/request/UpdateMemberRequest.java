package com.mung.member.request;

import com.mung.common.domain.Validate;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UpdateMemberRequest {

    @Size(min = 11,
            max = 11,
            message = Validate.MESSAGE.VALID_TEL)
    private String tel;

    private String password;
    private String zipcode;
    private String city;
    private String street;

    @Builder
    public UpdateMemberRequest(String tel, String password, String zipcode, String city, String street) {
        this.tel = tel;
        this.password = password;
        this.zipcode = zipcode;
        this.city = city;
        this.street = street;
    }
}
