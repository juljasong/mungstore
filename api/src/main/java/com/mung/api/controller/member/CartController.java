package com.mung.api.controller.member;

import com.mung.common.response.MessageResponse;
import com.mung.member.dto.CartDto.AddCartDto;
import com.mung.member.dto.CartDto.DeleteCartDto;
import com.mung.member.service.CartService;
import jakarta.servlet.http.HttpServletRequest;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
    public MessageResponse<?> getCart(HttpServletRequest request) {
        String jwt = request.getHeader("Authorization").replace("Bearer ", "");

        return MessageResponse.builder()
            .data(cartService.getCart(jwt))
            .build();
    }

    @PostMapping
    public MessageResponse<?> addCartItem(HttpServletRequest request,
        @RequestBody List<AddCartDto> addCartRequest) {
        String jwt = request.getHeader("Authorization").replace("Bearer ", "");
        cartService.addCartItem(jwt, addCartRequest);

        return MessageResponse.ofSuccess();
    }

    @DeleteMapping
    public MessageResponse<?> deleteCartItem(HttpServletRequest request,
        @RequestBody List<DeleteCartDto> deleteCartDto) {
        String jwt = request.getHeader("Authorization").replace("Bearer ", "");

        cartService.deleteCartItem(jwt, deleteCartDto);
        return MessageResponse.ofSuccess();
    }

}
