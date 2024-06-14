package com.mung.member.service;

import com.mung.common.exception.BadRequestException;
import com.mung.member.config.JwtUtil;
import com.mung.member.domain.Cart;
import com.mung.member.dto.CartDto.AddCartDto;
import com.mung.member.dto.CartDto.CartResponse;
import com.mung.member.dto.CartDto.DeleteCartDto;
import com.mung.member.repository.CartRedisRepository;
import com.mung.product.service.OptionsService;
import com.mung.product.service.ProductService;
import com.mung.stock.exception.OutOfStockException;
import com.mung.stock.service.StockService;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class CartService {

    private final CartRedisRepository cartRedisRepository;
    private final ProductService productService;
    private final OptionsService optionsService;
    private final StockService stockService;
    private final JwtUtil jwtUtil;

    public void addCartItem(Long memberId, List<AddCartDto> addCartRequest) {

        for (AddCartDto request : addCartRequest) {
            productService.getProduct(request.getProductId())
                .orElseThrow(BadRequestException::new);
            optionsService.getOption(request.getOptionId())
                .orElseThrow(BadRequestException::new);
            if (!stockService.isStockAvailable(request.getOptionId(), request.getCount())) {
                throw new OutOfStockException();
            }
        }

        Cart cartByMember = cartRedisRepository.findById(memberId)
            .orElse(null);

        List<AddCartDto> newCartList;
        if (cartByMember == null) {
            newCartList = addCartRequest;
        } else {
            Map<Long, AddCartDto> cartMap = new HashMap<>();

            for (AddCartDto cartItem : cartByMember.getCartList()) {
                cartMap.put(cartItem.getOptionId(), cartItem);
            }

            for (AddCartDto requestItem : addCartRequest) {
                if (cartMap.containsKey(requestItem.getOptionId())) {
                    cartMap.get(requestItem.getOptionId())
                        .addCount(requestItem.getCount());
                } else {
                    cartMap.put(requestItem.getOptionId(), requestItem);
                }
            }
            newCartList = new ArrayList<>(cartMap.values());
        }

        saveNewCartList(memberId, newCartList);
    }

    public void deleteCartItem(Long memberId, List<DeleteCartDto> deleteCartRequest) {
        Cart cartByMember = cartRedisRepository.findById(memberId)
            .orElse(null);
        List<AddCartDto> newCartList;

        if (cartByMember == null) {
            return;
        } else {
            newCartList = cartByMember.getCartList();
            int size = newCartList.size();

            for (DeleteCartDto deleteItem : deleteCartRequest) {

                for (int i = 0; i < size; i++) {
                    AddCartDto cartDto = newCartList.get(i);

                    if (Objects.equals(cartDto.getProductId(), deleteItem.getProductId())
                        && Objects.equals(cartDto.getOptionId(), deleteItem.getOptionId())) {
                        newCartList.remove(i);
                        break;
                    }

                }

            }

        }

        saveNewCartList(memberId, newCartList);
    }

    private void saveNewCartList(Long memberId, List<AddCartDto> newCartList) {
        cartRedisRepository.deleteById(memberId);

        if (!newCartList.isEmpty()) {
            cartRedisRepository.save(Cart.builder()
                .memberId(memberId)
                .cartList(newCartList)
                .build());
        }
    }

    public List<CartResponse> getCart(Long memberId) {
        Cart cart = cartRedisRepository.findById(memberId)
            .orElse(null);

        if (cart == null) {
            return Collections.emptyList();
        } else {
            List<AddCartDto> cartList = cart.getCartList();
            List<CartResponse> cartResponseList = new ArrayList<>();

            for (AddCartDto item : cartList) {
                productService.getProductIdAndOptionId(item.getProductId(), item.getOptionId())
                    .forEach(
                        object -> cartResponseList.add(new CartResponse(object, item.getCount()))
                    );
            }

            return cartResponseList;
        }
    }
}
