package com.mung.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;

import com.mung.common.domain.Address;
import com.mung.member.config.JwtUtil;
import com.mung.member.domain.Member;
import com.mung.member.repository.MemberRepository;
import com.mung.order.domain.Orders;
import com.mung.order.dto.OrderDto.Order;
import com.mung.order.dto.OrderDto.OrderRequest;
import com.mung.order.dto.OrderDto.OrderResponse;
import com.mung.order.repository.OrderRepository;
import com.mung.product.domain.Options;
import com.mung.product.domain.Product;
import com.mung.product.service.OptionsService;
import com.mung.product.service.ProductService;
import com.mung.stock.domain.Stock;
import com.mung.stock.exception.OutOfStockException;
import com.mung.stock.service.StockService;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

@ExtendWith(MockitoExtension.class)
class OrderServiceTest {

    @InjectMocks
    OrderService orderService;

    @Mock
    OrderRepository orderRepository;
    @Mock
    MemberRepository memberRepository;
    @Mock
    ProductService productService;
    @Mock
    StockService stockService;
    @Mock
    OptionsService optionsService;

    @Mock
    JwtUtil jwtUtil;


    @Test
    public void 주문_성공() {
        // given
        given(jwtUtil.getMemberId(anyString()))
            .willReturn(1L);
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(Member.builder().build()));
        given(productService.getProduct(anyLong()))
            .willReturn(Product.builder().build());
        given(stockService.getStock(anyLong()))
            .willReturn(Stock.builder().quantity(900).build());
        given(optionsService.getOption(anyLong()))
            .willReturn(Options.builder().build());

        Orders order = Orders.builder().build();
        ReflectionTestUtils.setField(order, "id", 1L);
        given(orderRepository.save(any()))
            .willReturn(order);

        List<Order> orders = new ArrayList<>();
        orders.add(Order.builder()
            .productId(1L)
            .productName("pname1")
            .optionId(1L)
            .quantity(2)
            .orderPrice(1200)
            .build());

        OrderRequest orderReq = OrderRequest.builder()
            .orders(orders)
            .totalPrice(2700)
            .tel1("01011111111")
            .tel2("01011112222")
            .address(new Address("12345", "시티", "스트릿"))
            .build();

        // when
        OrderResponse response = orderService.order(orderReq, "jwt");

        // then
        assertEquals(1L, response.getOrderId());
    }

    @Test
    public void 주문_실패_재고부족() {
        // given
        given(jwtUtil.getMemberId(anyString()))
            .willReturn(1L);
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(Member.builder().build()));
        given(productService.getProduct(anyLong()))
            .willReturn(Product.builder().build());
        given(stockService.getStock(anyLong()))
            .willReturn(Stock.builder().quantity(1).build());
        given(optionsService.getOption(anyLong()))
            .willReturn(Options.builder().build());

        List<Order> orders = new ArrayList<>();
        orders.add(Order.builder()
            .productId(1L)
            .productName("pname1")
            .optionId(1L)
            .quantity(2)
            .orderPrice(1200)
            .build());

        OrderRequest orderReq = OrderRequest.builder()
            .orders(orders)
            .totalPrice(2700)
            .tel1("01011111111")
            .tel2("01011112222")
            .address(new Address("12345", "시티", "스트릿"))
            .build();

        // expected
        assertThrows(OutOfStockException.class,
            () -> orderService.order(orderReq, "jwt"));
    }


}