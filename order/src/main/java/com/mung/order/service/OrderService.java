package com.mung.order.service;

import com.mung.common.domain.Address;
import com.mung.common.exception.BadRequestException;
import com.mung.common.exception.NotExistMemberException;
import com.mung.member.config.JwtUtil;
import com.mung.member.domain.Member;
import com.mung.member.repository.MemberRepository;
import com.mung.order.domain.Delivery;
import com.mung.order.domain.DeliveryStatus;
import com.mung.order.domain.OrderItem;
import com.mung.order.domain.OrderStatus;
import com.mung.order.domain.Orders;
import com.mung.order.dto.AddressDto.DeliveryAddressDto;
import com.mung.order.dto.OrderDto.GetOrderResponse;
import com.mung.order.dto.OrderDto.GetOrdersResponse;
import com.mung.order.dto.OrderDto.OrderCancelRequest;
import com.mung.order.dto.OrderDto.OrderItemDto;
import com.mung.order.dto.OrderDto.OrderRequest;
import com.mung.order.dto.OrderDto.OrderResponse;
import com.mung.order.dto.OrderDto.OrderSearchRequest;
import com.mung.order.repository.OrderRepository;
import com.mung.product.domain.Options;
import com.mung.product.domain.Product;
import com.mung.product.repository.OptionsRepository;
import com.mung.product.repository.ProductRepository;
import com.mung.stock.domain.Stock;
import com.mung.stock.repository.StockRepository;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ProductRepository productRepository;
    private final StockRepository stockRepository;
    private final OptionsRepository optionsRepository;

    private final JwtUtil jwtUtil;

    @Transactional
    public OrderResponse order(OrderRequest orderRequest, String jwt) {
        Member member = memberRepository.findById(jwtUtil.getMemberId(jwt))
            .orElseThrow(NotExistMemberException::new);

        Delivery delivery = createDelivery(orderRequest, orderRequest.getAddress());
        List<OrderItem> orderItems = createOrderItems(orderRequest, member);
        Orders order = Orders.createOrder(member, delivery, orderItems);

        Orders savedOrder = orderRepository.save(order);
        return OrderResponse.builder()
            .orderId(savedOrder.getId())
            .build();
    }

    @Transactional
    public void cancelOrder(OrderCancelRequest orderCancelRequest, String jwt) {
        Long memberId = jwtUtil.getMemberId(jwt);

        Orders order = orderRepository.findById(orderCancelRequest.getOrderId())
            .orElseThrow(BadRequestException::new);

        if (!Objects.equals(memberId, order.getMember().getId())) {
            throw new BadRequestException();
        }

        order.cancel();
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
        List<OrderItemDto> orders = orderRequest.getOrderItems();
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemDto order : orders) {
            Product product = productRepository.findById(order.getProductId())
                .orElseThrow(BadRequestException::new);
            Stock stock = stockRepository.findByOptionId(order.getOptionId())
                .orElseThrow(BadRequestException::new);
            Options option = optionsRepository.findById(order.getOptionId())
                .orElseThrow(BadRequestException::new);

            orderItems.add(OrderItem.builder()
                .product(product)
                .stock(stock)
                .options(option)
                .member(member)
                .orderPrice(order.getOrderPrice())
                .quantity(order.getQuantity())
                .contents(order.getContents())
                .status(OrderStatus.PAYMENT_PENDING)
                .build());

            stock.removeStock(order.getQuantity());
        }
        return orderItems;
    }

    public GetOrderResponse getOrder(Long orderId, String jwt) {
        Long memberId = jwtUtil.getMemberId(jwt);

        Orders order = orderRepository.findById(orderId)
            .orElseThrow(BadRequestException::new);

        if (!Objects.equals(order.getMember().getId(), memberId)) {
            throw new BadRequestException();
        }

        List<OrderItemDto> orderItems = order.getOrderItems().stream()
            .map(OrderItemDto::new)
            .collect(Collectors.toList());

        return GetOrderResponse.builder()
            .orderId(order.getId())
            .orderItems(orderItems)
            .deliveryAddress(new DeliveryAddressDto(order.getDelivery()))
            .orderStatus(order.getStatus())
            .totalPrice(order.getTotalPrice())
            .build();
    }

    public Page<GetOrdersResponse> getOrders(OrderSearchRequest condition, String jwt) {
        Long memberId = jwtUtil.getMemberId(jwt);

        if (!Objects.equals(condition.getMemberId(), memberId)) {
            throw new BadRequestException();
        }

        PageRequest pageRequest = PageRequest.of(condition.getPageNumber(),
            condition.getPageSize());
        return orderRepository.search(condition, pageRequest);
    }
}
