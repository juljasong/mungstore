package com.mung.api.controller.order;

import com.mung.api.config.auth.PrincipalDetails;
import com.mung.common.response.MessageResponse;
import com.mung.order.dto.OrderDto.GetOrderResponse;
import com.mung.order.dto.OrderDto.GetOrdersResponse;
import com.mung.order.dto.OrderDto.OrderSearchRequest;
import com.mung.order.service.OrderService;
import com.mung.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;
    private final PaymentService paymentService;

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

        GetOrderResponse orderResponse = orderService.getOrderResponse(orderId, memberId);
        return MessageResponse.builder()
            .data(orderResponse)
            .build();
    }

//    @PatchMapping("/order")
//    public MessageResponse<?> cancelOrder(@RequestBody OrderCancelRequest orderCancelRequest) {
//        Long memberId = ((PrincipalDetails) (SecurityContextHolder.getContext()
//            .getAuthentication()).getPrincipal()).getMemberId();
//
//        Long orderId = orderService.cancelOrder(orderCancelRequest, memberId);
//        CancelPaymentResponse response = paymentService.cancelPayment(CancelPaymentRequest.builder()
//                .orderId(orderId).build(),
//            memberId);
//
//        return MessageResponse.builder()
//            .data(response)
//            .build();
//    }

}
