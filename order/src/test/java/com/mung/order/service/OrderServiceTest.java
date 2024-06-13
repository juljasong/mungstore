package com.mung.order.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.any;
import static org.mockito.BDDMockito.anyLong;
import static org.mockito.BDDMockito.anyString;
import static org.mockito.BDDMockito.given;

import com.mung.common.domain.Address;
import com.mung.common.exception.BadRequestException;
import com.mung.member.config.JwtUtil;
import com.mung.member.domain.Member;
import com.mung.member.repository.MemberRepository;
import com.mung.order.domain.Delivery;
import com.mung.order.domain.DeliveryStatus;
import com.mung.order.domain.OrderStatus;
import com.mung.order.domain.Orders;
import com.mung.order.dto.OrderDto.GetOrderResponse;
import com.mung.order.dto.OrderDto.OrderCancelRequest;
import com.mung.order.dto.OrderDto.OrderItemDto;
import com.mung.order.dto.OrderDto.OrderRequest;
import com.mung.order.dto.OrderDto.OrderResponse;
import com.mung.order.dto.OrderDto.OrderSearchRequest;
import com.mung.order.exception.AlreadyCancelledException;
import com.mung.order.exception.AlreadyDeliveredException;
import com.mung.order.repository.OrderRepository;
import com.mung.product.domain.Options;
import com.mung.product.domain.Product;
import com.mung.product.repository.OptionsRepository;
import com.mung.product.repository.ProductRepository;
import com.mung.stock.domain.Stock;
import com.mung.stock.exception.OutOfStockException;
import com.mung.stock.repository.StockRepository;
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
    ProductRepository productRepository;
    @Mock
    StockRepository stockRepository;
    @Mock
    OptionsRepository optionsRepository;

    @Mock
    JwtUtil jwtUtil;

    @Test
    public void 주문_성공() {
        // given
        given(jwtUtil.getMemberId(anyString()))
            .willReturn(1L);
        given(memberRepository.findById(anyLong()))
            .willReturn(Optional.of(Member.builder().build()));
        given(productRepository.findById(anyLong()))
            .willReturn(Optional.of(Product.builder().build()));
        given(stockRepository.findByOptionId(anyLong()))
            .willReturn(Optional.of(Stock.builder().quantity(10).build()));
        given(optionsRepository.findById(anyLong()))
            .willReturn(Optional.of(Options.builder().build()));

        Orders order = Orders.builder().build();
        ReflectionTestUtils.setField(order, "id", 1L);
        given(orderRepository.save(any()))
            .willReturn(order);

        List<OrderItemDto> orders = new ArrayList<>();
        orders.add(OrderItemDto.builder()
            .productId(1L)
            .productName("pname1")
            .optionId(1L)
            .quantity(2)
            .orderPrice(30000)
            .build());

        OrderRequest orderReq = OrderRequest.builder()
            .orderItems(orders)
            .totalPrice(60000)
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
        given(productRepository.findById(anyLong()))
            .willReturn(Optional.of(Product.builder().build()));
        given(stockRepository.findByOptionId(anyLong()))
            .willReturn(Optional.of(Stock.builder().quantity(1).build()));
        given(optionsRepository.findById(anyLong()))
            .willReturn(Optional.of(Options.builder().build()));

        List<OrderItemDto> orders = new ArrayList<>();
        orders.add(OrderItemDto.builder()
            .productId(1L)
            .productName("pname1")
            .optionId(1L)
            .quantity(2)
            .orderPrice(1200)
            .build());

        OrderRequest orderReq = OrderRequest.builder()
            .orderItems(orders)
            .totalPrice(2700)
            .tel1("01011111111")
            .tel2("01011112222")
            .address(new Address("12345", "시티", "스트릿"))
            .build();

        // expected
        assertThrows(OutOfStockException.class,
            () -> orderService.order(orderReq, "jwt"));
    }

    @Test
    public void 주문취소_성공() {
        // given
        Member member = Member.builder().build();
        ReflectionTestUtils.setField(member, "id", 1L);

        Delivery delivery = Delivery.builder()
            .status(DeliveryStatus.READY)
            .build();

        given(jwtUtil.getMemberId(anyString()))
            .willReturn(1L);
        given(orderRepository.findById(anyLong()))
            .willReturn(Optional.of(Orders.builder()
                .member(member)
                .delivery(delivery)
                .build()));

        OrderCancelRequest request = OrderCancelRequest.builder().orderId(1L).build();

        // when
        orderService.cancelOrder(request, "Bearer test");
    }

    @Test
    public void 주문취소_실패_배송된주문() {
        // given
        Member member = Member.builder().build();
        ReflectionTestUtils.setField(member, "id", 1L);

        Delivery delivery = Delivery.builder()
            .status(DeliveryStatus.SHIPPED)
            .build();

        given(jwtUtil.getMemberId(anyString()))
            .willReturn(1L);
        given(orderRepository.findById(anyLong()))
            .willReturn(Optional.of(Orders.builder()
                .member(member)
                .delivery(delivery)
                .build()));

        OrderCancelRequest request = OrderCancelRequest.builder().orderId(1L).build();

        // when
        assertThrows(AlreadyDeliveredException.class,
            () -> orderService.cancelOrder(request, "Bearer test"));
    }

    @Test
    public void 주문취소_실패_취소된주문() {
        // given
        Member member = Member.builder().build();
        ReflectionTestUtils.setField(member, "id", 1L);

        Delivery delivery = Delivery.builder()
            .status(DeliveryStatus.READY)
            .build();

        given(jwtUtil.getMemberId(anyString()))
            .willReturn(1L);
        given(orderRepository.findById(anyLong()))
            .willReturn(Optional.of(Orders.builder()
                .member(member)
                .delivery(delivery)
                .status(OrderStatus.CANCELLED)
                .build()));

        OrderCancelRequest request = OrderCancelRequest.builder()
            .orderId(1L)
            .build();

        // when
        assertThrows(AlreadyCancelledException.class,
            () -> orderService.cancelOrder(request, "Bearer test"));
    }

    @Test
    public void 주문취소_실패_다른유저() {
        // given
        Member member = Member.builder().build();
        ReflectionTestUtils.setField(member, "id", 2L);

        Delivery delivery = Delivery.builder()
            .status(DeliveryStatus.READY)
            .build();

        given(jwtUtil.getMemberId(anyString()))
            .willReturn(1L);
        given(orderRepository.findById(anyLong()))
            .willReturn(Optional.of(Orders.builder()
                .member(member)
                .delivery(delivery)
                .build()));

        OrderCancelRequest request = OrderCancelRequest.builder()
            .orderId(1L)
            .build();

        // when
        assertThrows(BadRequestException.class,
            () -> orderService.cancelOrder(request, "Bearer test"));
    }

    @Test
    public void 주문조회_단건_성공() {
        // given
        given(jwtUtil.getMemberId(anyString()))
            .willReturn(1L);

        Member member = Member.builder().build();
        ReflectionTestUtils.setField(member, "id", 1L);

        given(orderRepository.findById(anyLong()))
            .willReturn(Optional.of(Orders.builder()
                .member(member)
                .delivery(Delivery.builder()
                    .address(new Address("12345", "city", "street"))
                    .build())
                .build()));

        // when
        GetOrderResponse response = orderService.getOrder(1L, "test");

        // then
        System.out.println("response = " + response);
    }

    @Test
    public void 주문조회_단건_실패_다른유저() {
        // given
        given(jwtUtil.getMemberId(anyString()))
            .willReturn(123L);

        Member member = Member.builder().build();
        ReflectionTestUtils.setField(member, "id", 1L);

        given(orderRepository.findById(anyLong()))
            .willReturn(Optional.of(Orders.builder()
                .member(member)
                .delivery(Delivery.builder()
                    .address(new Address("12345", "city", "street"))
                    .build())
                .build()));

        // expected
        assertThrows(BadRequestException.class,
            () -> orderService.getOrder(1L, "test"));
    }

    @Test
    public void 주문조회_단건_실패_없는주문() {
        // given
        given(jwtUtil.getMemberId(anyString()))
            .willReturn(123L);

        given(orderRepository.findById(anyLong()))
            .willReturn(Optional.empty());

        // expected
        assertThrows(BadRequestException.class,
            () -> orderService.getOrder(1L, "test"));
    }

    @Test
    public void 주문조회_리스트_실패_다른유저() throws Exception {
        // given
        given(jwtUtil.getMemberId(anyString()))
            .willReturn(123L);

        OrderSearchRequest request = OrderSearchRequest.builder()
            .memberId(1L)
            .build();

        // expected
        assertThrows(BadRequestException.class,
            () -> orderService.getOrders(request, "test"));
    }
}