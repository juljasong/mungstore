package com.mung.api.controller.payment;

import com.mung.api.config.auth.PrincipalDetails;
import com.mung.common.response.MessageResponse;
import com.mung.payment.dto.PaymentDto.CompletePaymentDto;
import com.mung.payment.dto.PaymentDto.KaKaoCompletePaymentRequest;
import com.mung.payment.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/payment")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/kakao/complete")
    public MessageResponse<?> kakaoComplete(@RequestBody KaKaoCompletePaymentRequest request) {
        Long memberId = ((PrincipalDetails) (SecurityContextHolder.getContext()
            .getAuthentication()).getPrincipal()).getMemberId();

        CompletePaymentDto response = paymentService.kakaoComplete(request, memberId);

        return MessageResponse.builder()
            .code(response.getMessage() == null ? HttpStatus.OK.value()
                : HttpStatus.INTERNAL_SERVER_ERROR.value())
            .message(response.getMessage() == null ? HttpStatus.OK.getReasonPhrase()
                : HttpStatus.INTERNAL_SERVER_ERROR.getReasonPhrase())
            .data(response)
            .build();
    }
}
