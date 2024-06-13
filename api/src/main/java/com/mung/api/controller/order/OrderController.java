package com.mung.api.controller.order;

import com.mung.common.response.MessageResponse;
import com.mung.order.dto.OrderDto.GetOrderResponse;
import com.mung.order.dto.OrderDto.GetOrdersResponse;
import com.mung.order.dto.OrderDto.OrderCancelRequest;
import com.mung.order.dto.OrderDto.OrderRequest;
import com.mung.order.dto.OrderDto.OrderResponse;
import com.mung.order.dto.OrderDto.OrderSearchRequest;
import com.mung.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
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
    public MessageResponse<?> getOrders(@RequestBody OrderSearchRequest orderSearchRequest, HttpServletRequest request) {
        String jwt = request.getHeader("Authorization").replace("Bearer ", "");

        Page<GetOrdersResponse> getOrdersResponse = orderService.getOrders(orderSearchRequest, jwt);
        return MessageResponse.builder()
            .data(getOrdersResponse)
            .build();
    }

    @GetMapping("/order")
    public MessageResponse<?> getOrder(@RequestParam Long orderId, HttpServletRequest request) {
        String jwt = request.getHeader("Authorization").replace("Bearer ", "");

        GetOrderResponse orderResponse = orderService.getOrder(orderId, jwt);
        return MessageResponse.builder()
            .data(orderResponse)
            .build();
    }

    @PostMapping("/order")
    public MessageResponse<?> order(@RequestBody OrderRequest orderRequest,
        HttpServletRequest request) {
        String jwt = request.getHeader("Authorization").replace("Bearer ", "");

        OrderResponse order = orderService.order(orderRequest, jwt);
        return MessageResponse.builder()
            .data(order)
            .build();
    }

    @PatchMapping("/order")
    public MessageResponse<?> cancelOrder(@RequestBody OrderCancelRequest orderCancelRequest,
        HttpServletRequest request) {
        String jwt = request.getHeader("Authorization").replace("Bearer ", "");

        orderService.cancelOrder(orderCancelRequest, jwt);
        return MessageResponse.ofSuccess();
    }

}
