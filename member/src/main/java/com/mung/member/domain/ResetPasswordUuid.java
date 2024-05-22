package com.mung.member.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Builder
@Getter
@AllArgsConstructor
@NoArgsConstructor
@RedisHash(value = "reset", timeToLive = 60 * 60L)
public class ResetPasswordUuid {

    @Id
    private String uuid;

    private Long memberId;

}
