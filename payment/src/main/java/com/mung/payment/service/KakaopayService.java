package com.mung.payment.service;

import com.mung.order.domain.OrderItem;
import com.mung.order.domain.Orders;
import com.mung.order.dto.OrderDto.OrderRequest;
import com.mung.payment.domain.KakaopayPayment;
import com.mung.payment.domain.Payment;
import com.mung.payment.domain.PaymentKakaoLog;
import com.mung.payment.dto.KakaopayDto.KakaopayApproveRequest;
import com.mung.payment.dto.KakaopayDto.KakaopayApproveResponse;
import com.mung.payment.dto.KakaopayDto.KakaopayCancelRequest;
import com.mung.payment.dto.KakaopayDto.KakaopayCancelResponse;
import com.mung.payment.dto.KakaopayDto.KakaopayReadyRequest;
import com.mung.payment.dto.KakaopayDto.KakaopayReadyResponse;
import com.mung.payment.dto.PaymentDto.CancelPaymentResponse;
import com.mung.payment.repository.KakaopayLogRepository;
import java.util.Objects;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

@Slf4j
@Service
@RequiredArgsConstructor
public class KakaopayService {

    private final KakaopayLogRepository kakaoPayLogRepository;

    @Value("${kakao.dev.secret-key}")
    private String kakaopaySecretKey;

    @Value("${kakao.dev.cid}")
    private String cid;

    @Value("${kakao.dev.host}")
    private String host;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public KakaopayReadyResponse ready(String agent, OrderRequest orderRequest, Orders order,
        Long memberId) {
        // Request header
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "SECRET_KEY " + kakaopaySecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Request param
        KakaopayReadyRequest readyRequest = KakaopayReadyRequest.builder()
            .cid(cid)
            .partnerOrderId(String.valueOf(order.getId()))
            .partnerUserId(String.valueOf(memberId))
            .itemName(String.format("%s 외 %d건",
                orderRequest.getOrderItems().get(0).getProductName(),
                orderRequest.getOrderItems().size() - 1))
            .quantity(order.getOrderItems().stream()
                .mapToInt(OrderItem::getQuantity)
                .sum())
            .totalAmount(order.getTotalPrice())
            .taxFreeAmount(0)
            .vatAmount(0)
            .approvalUrl(host + "/kakaopay/approve/" + agent + "?id=" + order.getId())
            .cancelUrl(host + "/kakaopay/cancel/" + agent)
            .failUrl(host + "/kakaopay/fail/" + agent)
            .build();
        log.info("KakaopayService.ready :: request params : {}", readyRequest.toString());

        // Send reqeust
        HttpEntity<KakaopayReadyRequest> entityMap = new HttpEntity<>(readyRequest, headers);
        ResponseEntity<KakaopayReadyResponse> response = new RestTemplate().postForEntity(
            "https://open-api.kakaopay.com/online/v1/payment/ready",
            entityMap,
            KakaopayReadyResponse.class
        );
        log.info("KakaopayService.ready :: response : {}", response);

        return response.getBody();
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public KakaopayApproveResponse approve(String pgToken, KakaopayPayment kakaopayPayment) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "SECRET_KEY " + kakaopaySecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Request param
        KakaopayApproveRequest approveRequest = KakaopayApproveRequest.builder()
            .cid(cid)
            .tid(kakaopayPayment.getTid())
            .partnerOrderId(String.valueOf(kakaopayPayment.getOrderId()))
            .partnerUserId(String.valueOf(kakaopayPayment.getMemberId()))
            .pgToken(pgToken)
            .build();
        log.info("KakaopayService.approve :: request params : {}", approveRequest.toString());

        try {
            // Send Request
            HttpEntity<KakaopayApproveRequest> entityMap = new HttpEntity<>(approveRequest, headers);
            ResponseEntity<KakaopayApproveResponse> response = new RestTemplate().postForEntity(
                "https://open-api.kakaopay.com/online/v1/payment/approve",
                entityMap,
                KakaopayApproveResponse.class
            );
            log.info("KakaopayService.approve :: response : {}", response);
            kakaoPayLogRepository.save(new PaymentKakaoLog(Objects.requireNonNull(response.getBody())));

            return response.getBody();
        } catch (HttpStatusCodeException ex) {
            log.error(ex.getResponseBodyAsString());
            throw new RuntimeException();
        }
    }

    public KakaopayCancelResponse cancel(Payment payment) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "SECRET_KEY " + kakaopaySecretKey);
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Request param
        KakaopayCancelRequest cancelRequest = KakaopayCancelRequest.builder()
            .cid(cid)
            //.cidSecret()
            .tid(payment.getTid())
            .cancelAmount(payment.getTotalAmount())
            .cancelTaxFreeAmount(payment.getTaxFree())
            .cancelVatAmount(payment.getVat())
            //.cancelAvailableAmount()
            //.payload()
            .build();
        log.info("KakaopayService.cancel :: request params : {}", cancelRequest.toString());

        try {
            // Send Request
            HttpEntity<KakaopayCancelRequest> entityMap = new HttpEntity<>(cancelRequest, headers);
            ResponseEntity<KakaopayCancelResponse> response = new RestTemplate().postForEntity(
                "https://open-api.kakaopay.com/online/v1/payment/cancel",
                entityMap,
                KakaopayCancelResponse.class
            );
            log.info("KakaopayService.cancel :: response : {}", response);
            kakaoPayLogRepository.save(new PaymentKakaoLog(Objects.requireNonNull(response.getBody())));

            return response.getBody();
        } catch (HttpStatusCodeException ex) {
            log.error("KakaopayService.cancel :: HttpStatusCodeException : {}", ex.getResponseBodyAsString());
            throw new RuntimeException();
        }
    }

}
