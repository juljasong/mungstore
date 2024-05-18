package com.mung.member.dto;

import lombok.*;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LoginDto {

    private String accessToken;
    private String refreshToken;
    private Long memberId;

    @Builder
    public LoginDto(String accessToken, String refreshToken, Long memberId) {
        this.accessToken = accessToken;
        this.refreshToken = refreshToken;
        this.memberId = memberId;
    }
}
