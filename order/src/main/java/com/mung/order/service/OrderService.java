package com.mung.order.service;

import com.mung.common.domain.Address;
import com.mung.common.exception.BadRequestException;
import com.mung.common.exception.NotExistMemberException;
import com.mung.kafka.domain.KafkaTopic;
import com.mung.kafka.dto.PaymentCancelDto.PaymentCancelRequest;
import com.mung.kafka.producer.PaymentCancelSender;
import com.mung.member.domain.Member;
import com.mung.member.dto.CartDto.DeleteCartDto;
import com.mung.member.exception.Unauthorized;
import com.mung.member.repository.MemberRepository;
import com.mung.member.service.CartService;
import com.mung.order.domain.Delivery;
import com.mung.order.domain.DeliveryStatus;
import com.mung.order.domain.OrderItem;
import com.mung.order.domain.OrderStatus;
import com.mung.order.domain.Orders;
import com.mung.order.dto.AddressDto.DeliveryAddressDto;
import com.mung.order.dto.OrderDto.GetOrderResponse;
import com.mung.order.dto.OrderDto.GetOrdersResponse;
import com.mung.order.dto.OrderDto.OrderCancelRequest;
import com.mung.order.dto.OrderDto.OrderCancelResponse;
import com.mung.order.dto.OrderDto.OrderItemDto;
import com.mung.order.dto.OrderDto.OrderRequest;
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
    private final CartService cartService;
    private final PaymentCancelSender paymentCancelSender;

    @Transactional
    public Orders requestOrder(OrderRequest orderRequest, Long memberId) {
        Member member = memberRepository.findById(memberId)
            .orElseThrow(NotExistMemberException::new);

        deleteOrderedItemInCart(memberId, orderRequest);

        Delivery delivery = createDelivery(orderRequest,
            new Address(orderRequest.getZipcode(), orderRequest.getCity(),
                orderRequest.getStreet()));
        List<OrderItem> orderItems = createOrderItems(orderRequest, member);
        Orders order = createOrder(member, delivery, orderItems);

        return orderRepository.save(order);
    }

    private void deleteOrderedItemInCart(Long memberId, OrderRequest orderRequest) {
        List<OrderItemDto> orderItems = orderRequest.getOrderItems();
        List<DeleteCartDto> deleteCartDtoList = new ArrayList<>();
        for (OrderItemDto orderItemDto : orderItems) {
            deleteCartDtoList.add(DeleteCartDto.builder()
                .productId(orderItemDto.getProductId())
                .optionId(orderItemDto.getOptionId())
                .build());
        }
        cartService.deleteCartItem(memberId, deleteCartDtoList);
    }

    @Transactional
    public OrderCancelResponse cancelOrder(OrderCancelRequest orderCancelRequest, Long memberId) {

        Orders order = orderRepository.findById(orderCancelRequest.getOrderId())
            .orElseThrow(BadRequestException::new);

        if (!Objects.equals(memberId, order.getMember().getId())) {
            throw new Unauthorized();
        }

        log.info("OrderService.cancelOrder :: 주문번호 {} :: order.cancel", order.getId());
        order.cancel();
        increaseStock(order);

        log.info("OrderService.cancelOrder :: 주문번호 {} :: paymentCancel 요청", order.getId());
        paymentCancelSender.sendMessage(KafkaTopic.PAYMENT_CANCEL_REQUEST,
            PaymentCancelRequest.builder()
                .orderId(order.getId())
                .memberId(memberId)
                .build());

        return OrderCancelResponse.builder()
            .orderId(order.getId())
            .build();
    }

    private Delivery createDelivery(OrderRequest orderRequest, Address address) {
        return Delivery.builder()
            .tel1(orderRequest.getTel1())
            .tel2(orderRequest.getTel2())
            .address(address)
            .status(DeliveryStatus.READY)
            .build();
    }

    private Orders createOrder(Member member, Delivery delivery, List<OrderItem> orderItems) {
        Orders order = Orders.builder()
            .member(member)
            .delivery(delivery)
            .status(OrderStatus.PAYMENT_PENDING)
            .build();

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setTotalPrice(order.calcTotalPrice());

        return order;
    }

    private List<OrderItem> createOrderItems(OrderRequest orderRequest, Member member) {

        List<OrderItemDto> orders = orderRequest.getOrderItems();
        List<OrderItem> orderItems = new ArrayList<>();
        for (OrderItemDto order : orders) {
            Product product = productRepository.findById(order.getProductId())
                .orElseThrow(BadRequestException::new);
            Options option = optionsRepository.findById(order.getOptionId())
                .orElseThrow(BadRequestException::new);

            orderItems.add(OrderItem.builder()
                .product(product)
                .options(option)
                .member(member)
                .orderPrice(order.getOrderPrice())
                .quantity(order.getQuantity())
                .contents(order.getContents())
                .status(OrderStatus.PAYMENT_PENDING)
                .build());
        }
        return orderItems;
    }

    @Transactional
    public void decreaseStock(Orders order) {
        order.getOrderItems().forEach(orderItem -> {
            Stock stock = stockRepository.findByOptionId(orderItem.getOptions().getId())
                .orElseThrow(BadRequestException::new);
            stock.removeStock(orderItem.getQuantity());
        });
    }

    @Transactional
    public void increaseStock(Orders order) {
        order.getOrderItems().forEach(orderItem -> {
            Stock stock = stockRepository.findByOptionId(orderItem.getOptions().getId())
                .orElseThrow(BadRequestException::new);
            stock.addStock(orderItem.getQuantity());
        });
    }

    public GetOrderResponse getOrderResponse(Long orderId, Long memberId) {
        Orders order = orderRepository.findById(orderId)
            .orElseThrow(BadRequestException::new);

        if (!Objects.equals(order.getMember().getId(), memberId)) {
            throw new Unauthorized();
        }

        List<OrderItemDto> orderItems = order.getOrderItems().stream()
            .map(OrderItemDto::new)
            .collect(Collectors.toList());

        return GetOrderResponse.builder()
            .orderId(order.getId())
            .orderItems(orderItems)
            .deliveryAddress(new DeliveryAddressDto(order.getDelivery()))
            .orderStatus(order.getStatus())
            .totalPrice(order.calcTotalPrice())
            .build();
    }

    public Page<GetOrdersResponse> getOrders(OrderSearchRequest condition, Long memberId) {
        if (!Objects.equals(condition.getMemberId(), memberId)) {
            throw new Unauthorized();
        }

        PageRequest pageRequest = PageRequest.of(condition.getPageNumber(),
            condition.getPageSize());
        return orderRepository.search(condition, pageRequest);
    }

    public Orders getOrder(Long orderId, Long memberId) {
        Orders order = orderRepository.findById(orderId)
            .orElseThrow(BadRequestException::new);

        if (!Objects.equals(order.getMember().getId(), memberId)) {
            throw new Unauthorized();
        }

        return order;
    }

    @Transactional
    public Orders updateOrderStatus(Long orderId, OrderStatus status) {
        Orders order = orderRepository.findById(orderId)
            .orElseThrow(BadRequestException::new);
        order.updateStatus(status);
        return order;
    }
}
