package com.mung.api.controller.order;

import com.mung.common.response.MessageResponse;
import com.mung.order.dto.OrderDto.OrderCancelRequest;
import com.mung.order.dto.OrderDto.OrderRequest;
import com.mung.order.dto.OrderDto.OrderResponse;
import com.mung.order.service.OrderService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @GetMapping("/orders")
    public MessageResponse<?> getOrders() {

        return MessageResponse.builder()
            .data(null)
            .build();
    }

    @GetMapping("/order")
    public MessageResponse<?> getOrder() {

        return MessageResponse.builder()
            .data(null)
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
