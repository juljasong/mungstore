package com.mung.member.service;

import com.mung.member.config.JwtUtil;
import com.mung.member.domain.Cart;
import com.mung.member.dto.CartDto.AddCartDto;
import com.mung.member.dto.CartDto.DeleteCartDto;
import com.mung.member.repository.CartRedisRepository;
import com.mung.product.service.OptionsService;
import com.mung.product.service.ProductService;
import com.mung.stock.exception.OutOfStockException;
import com.mung.stock.service.StockService;
import java.util.List;
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

    public void addCartItem(String jwt, List<AddCartDto> addCartRequest) {
        Long memberId = jwtUtil.getMemberId(jwt);

        for (AddCartDto request : addCartRequest) {
            productService.getProduct(request.getProductId());
            optionsService.getOption(request.getOptionId());
            if (!stockService.isStockAvailable(request.getOptionId(), request.getCount())) {
                throw new OutOfStockException();
            }
        }

        Cart cartByMember = cartRedisRepository.findByMemberId(memberId);

        List<AddCartDto> newCartList;

        if (cartByMember == null) {
            newCartList = addCartRequest;
        } else {
            newCartList = cartByMember.getCartList();
            int size = newCartList.size();

            for (AddCartDto requestItem : addCartRequest) {

                boolean isContained = false;
                for (int i = 0; i < size; i++) {
                    AddCartDto cartDto = newCartList.get(i);

                    if (cartDto.equals(requestItem)) {
                        newCartList.remove(i);
                        cartDto.addCount(requestItem.getCount());
                        newCartList.add(cartDto);
                        isContained = true;
                        break;
                    }
                }

                if (!isContained) {
                    newCartList.add(requestItem);
                }

            }
        }

        saveNewCartList(memberId, newCartList);
    }

    public void deleteCartItem(String jwt, List<DeleteCartDto> deleteCartRequest) {
        Long memberId = jwtUtil.getMemberId(jwt);
        Cart cartByMember = cartRedisRepository.findByMemberId(memberId);
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
}
