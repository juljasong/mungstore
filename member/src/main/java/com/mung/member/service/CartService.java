package com.mung.member.service;

import com.mung.member.config.JwtUtil;
import com.mung.member.domain.Cart;
import com.mung.member.dto.CartDto.AddCartDto;
import com.mung.member.repository.CartRedisRepository;
import com.mung.product.service.OptionsService;
import com.mung.product.service.ProductService;
import com.mung.stock.exception.OutOfStockException;
import com.mung.stock.service.StockService;
import java.util.List;
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

    public void addCart(String jwt, List<AddCartDto> addCartRequest) {
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

        cartRedisRepository.deleteById(memberId);
        cartRedisRepository.save(Cart.builder()
            .memberId(memberId)
            .cartList(newCartList)
            .build());
    }

}
