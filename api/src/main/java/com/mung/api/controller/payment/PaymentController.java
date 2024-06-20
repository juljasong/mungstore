package com.mung.api.controller.payment;

import com.mung.api.config.auth.PrincipalDetails;
import com.mung.common.response.MessageResponse;
import com.mung.order.dto.OrderDto.OrderRequest;
import com.mung.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

//    @PostMapping("/cancel")
//    public MessageResponse<?> cancelPayment(@RequestBody CancelPaymentRequest request) {
//        Long memberId = ((PrincipalDetails) (SecurityContextHolder.getContext()
//            .getAuthentication()).getPrincipal()).getMemberId();
//
//        CancelPaymentResponse response = paymentService.cancelPayment(request, memberId);
//        return MessageResponse.builder()
//            .data(response)
//            .build();
//    }

    // 카카오페이 결제 요청
    @PostMapping("/kakaopay/ready/{agent}")
    public MessageResponse<?> ready(@PathVariable("agent") String agent,
        @RequestBody OrderRequest orderRequest) {
        Long memberId = ((PrincipalDetails) (SecurityContextHolder.getContext()
            .getAuthentication()).getPrincipal()).getMemberId();

        return MessageResponse.builder()
            .data(paymentService.readyKakaoPayment(agent, orderRequest, memberId))
            .build();
    }

    // 카카오페이 결제 승인
    @GetMapping("/kakaopay/approve/{agent}")
    public MessageResponse<?> approve(@PathVariable("agent") String agent,
        @RequestParam("pg_token") String pgToken, @RequestParam("id") Long orderId) {
        Long memberId = ((PrincipalDetails) (SecurityContextHolder.getContext()
            .getAuthentication()).getPrincipal()).getMemberId();

        return MessageResponse.builder()
            .data(paymentService.completeKakaoPayment(agent, pgToken, memberId, orderId))
            .build();
    }

    // 카카오페이 결제 취소
    @GetMapping("/kakaopay/cancel/{agent}")
    public MessageResponse<?> cancel(@PathVariable("agent") String agent) {
        // 주문건이 진짜 취소되었는지 확인 후 취소 처리
        // 결제내역조회(/v1/payment/status) api에서 status를 확인한다.
        return MessageResponse.ofSuccess();
    }

    // 카카오페이 결제 실패 시 redirect되는 url
    @GetMapping("/kakaopay/fail/{agent}")
    public MessageResponse<?> fail(@PathVariable("agent") String agent,
        @PathVariable("openType") String openType) {
        // 주문건이 진짜 실패되었는지 확인 후 실패 처리
        // 결제내역조회(/v1/payment/status) api에서 status를 확인한다.
        return MessageResponse.ofSuccess();
    }

}
