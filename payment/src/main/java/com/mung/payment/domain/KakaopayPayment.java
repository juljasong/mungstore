package com.mung.payment.domain;

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
@RedisHash(value = "kakaopay", timeToLive = 60 * 15L)
public class KakaopayPayment {

    @Id
    private Long orderId;
    private Long memberId;
    private String tid;


}
