package com.mung.member.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginResponse {

    private Long memberId;

    @Builder
    public LoginResponse(Long memberId) {
        this.memberId = memberId;
    }
}
