package com.mung.member.response;

import com.mung.member.domain.Address;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MyPageResponse {

    private Long memberId;
    private String email;
    private String tel;
    private Address address;

    @Builder
    public MyPageResponse(Long memberId, String email, String tel, Address address) {
        this.memberId = memberId;
        this.email = email;
        this.tel = tel;
        this.address = address;
    }
}
