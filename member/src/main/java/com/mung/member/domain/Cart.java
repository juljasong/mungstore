package com.mung.member.domain;

import com.mung.member.dto.CartDto.AddCartDto;
import java.util.List;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

@Getter
@NoArgsConstructor
@RedisHash(value = "cart", timeToLive = 60 * 60 * 24 * 7L)
public class Cart {

    @Id
    private Long memberId;

    private List<AddCartDto> cartList;

    @Builder
    public Cart(Long memberId, List<AddCartDto> cartList) {
        this.memberId = memberId;
        this.cartList = cartList;
    }
}


