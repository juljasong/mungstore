package com.mung.order.service;

import com.mung.common.domain.Address;
import com.mung.member.config.JwtUtil;
import com.mung.member.domain.Member;
import com.mung.member.service.MemberService;
import com.mung.order.domain.Delivery;
import com.mung.order.domain.DeliveryStatus;
import com.mung.order.domain.OrderItem;
import com.mung.order.domain.OrderStatus;
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
import com.mung.stock.service.StockService;
import java.util.ArrayList;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final MemberService memberService;
    private final ProductService productService;
    private final StockService stockService;
    private final OptionsService optionsService;
    private final OrderRepository orderRepository;
    private final JwtUtil jwtUtil;

    @Transactional
    public OrderResponse order(OrderRequest orderRequest, String jwt) {
        Member member = memberService.getMemberById(jwtUtil.getMemberId(jwt));

        Delivery delivery = createDelivery(orderRequest, orderRequest.getAddress());
        List<OrderItem> orderItems = createOrderItems(orderRequest, member);
        Orders order = Orders.createOrder(member, delivery, orderItems);

        Orders savedOrder = orderRepository.save(order);
        return OrderResponse.builder()
            .orderId(savedOrder.getId())
            .build();
    }

    private Delivery createDelivery(OrderRequest orderRequest, Address address) {
        return Delivery.builder()
            .tel1(orderRequest.getTel1())
            .tel2(orderRequest.getTel2())
            .address(new Address(address.getZipcode(), address.getCity(), address.getStreet()))
            .status(DeliveryStatus.READY)
            .build();
    }

    private List<OrderItem> createOrderItems(OrderRequest orderRequest, Member member) {
        List<Order> orders = orderRequest.getOrders();
        List<OrderItem> orderItems = new ArrayList<>();
        for (Order order : orders) {
            productService.getProduct(order.getProductId());
            Stock stock = stockService.getStock(order.getOptionId());
            optionsService.getOption(order.getOptionId());

            orderItems.add(OrderItem.builder()
                .productId(order.getProductId())
                .stockId(order.getOptionId())
                .optionId(order.getOptionId())
                .memberId(member.getId())
                .orderPrice(order.getOrderPrice())
                .quantity(order.getQuantity())
                .contents(order.getContents())
                .status(OrderStatus.PAYMENT_PENDING)
                .build());

            stock.removeStock(order.getQuantity());
        }
        return orderItems;
    }

}
