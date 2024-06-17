package com.mung.api.controller.order;

import com.mung.api.config.auth.PrincipalDetails;
import com.mung.common.response.MessageResponse;
import com.mung.order.dto.OrderDto.GetOrderResponse;
import com.mung.order.dto.OrderDto.GetOrdersResponse;
import com.mung.order.dto.OrderDto.OrderCancelRequest;
import com.mung.order.dto.OrderDto.OrderRequest;
import com.mung.order.dto.OrderDto.OrderResponse;
import com.mung.order.dto.OrderDto.OrderSearchRequest;
import com.mung.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/orders")
    public MessageResponse<?> getOrders(@RequestBody OrderSearchRequest orderSearchRequest) {
        Long memberId = ((PrincipalDetails) (SecurityContextHolder.getContext()
            .getAuthentication()).getPrincipal()).getMemberId();

        Page<GetOrdersResponse> getOrdersResponse = orderService.getOrders(orderSearchRequest,
            memberId);
        return MessageResponse.builder()
            .data(getOrdersResponse)
            .build();
    }

    @GetMapping("/order")
    public MessageResponse<?> getOrder(@RequestParam Long orderId) {
        Long memberId = ((PrincipalDetails) (SecurityContextHolder.getContext()
            .getAuthentication()).getPrincipal()).getMemberId();

        GetOrderResponse orderResponse = orderService.getOrder(orderId, memberId);
        return MessageResponse.builder()
            .data(orderResponse)
            .build();
    }

    @PostMapping("/order")
    public MessageResponse<?> requestOrder(@RequestBody OrderRequest orderRequest) {
        Long memberId = ((PrincipalDetails) (SecurityContextHolder.getContext()
            .getAuthentication()).getPrincipal()).getMemberId();

        OrderResponse order = orderService.requestOrder(orderRequest, memberId);
        return MessageResponse.builder()
            .data(order)
            .build();
    }

    @PatchMapping("/order")
    public MessageResponse<?> cancelOrder(@RequestBody OrderCancelRequest orderCancelRequest) {
        Long memberId = ((PrincipalDetails) (SecurityContextHolder.getContext()
            .getAuthentication()).getPrincipal()).getMemberId();

        orderService.cancelOrder(orderCancelRequest, memberId);
        return MessageResponse.ofSuccess();
    }

}
