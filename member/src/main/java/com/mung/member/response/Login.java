package com.mung.member.response;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Login {

    private Long memberId;

    @Builder
    public Login(Long memberId) {
        this.memberId = memberId;
    }
}
