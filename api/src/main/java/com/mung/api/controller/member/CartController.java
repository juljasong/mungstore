package com.mung.api.controller.member;

import com.mung.api.config.auth.PrincipalDetails;
import com.mung.common.response.MessageResponse;
import com.mung.member.dto.CartDto.AddCartDto;
import com.mung.member.dto.CartDto.DeleteCartDto;
import com.mung.member.service.CartService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    @GetMapping
    public MessageResponse<?> getCart() {
        Long memberId = ((PrincipalDetails) (SecurityContextHolder.getContext()
            .getAuthentication()).getPrincipal()).getMemberId();

        return MessageResponse.builder()
            .data(cartService.getCart(memberId))
            .build();
    }

    @PostMapping
    public MessageResponse<?> addCartItem(@RequestBody List<AddCartDto> addCartRequest) {
        Long memberId = ((PrincipalDetails) (SecurityContextHolder.getContext()
            .getAuthentication()).getPrincipal()).getMemberId();
        cartService.addCartItem(memberId, addCartRequest);

        return MessageResponse.ofSuccess();
    }

    @DeleteMapping
    public MessageResponse<?> deleteCartItem(@RequestBody List<DeleteCartDto> deleteCartDto) {
        Long memberId = ((PrincipalDetails) (SecurityContextHolder.getContext()
            .getAuthentication()).getPrincipal()).getMemberId();

        cartService.deleteCartItem(memberId, deleteCartDto);
        return MessageResponse.ofSuccess();
    }

}
