package com.mung.payment.domain;

import com.mung.common.domain.BaseEntity;
import com.mung.common.domain.PaymentMethod;
import com.mung.common.domain.PaymentProvider;
import com.mung.common.domain.PaymentStatus;
import com.mung.payment.dto.KakaopayDto.KakaopayApproveResponse;
import com.mung.payment.dto.KakaopayDto.KakaopayApproveResponse.CardInfo;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import java.util.Optional;
import java.util.function.Function;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Payment extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "payment_id")
    private Long id;

    private Long orderId;
    private String tid;
    private String approvalId;
    private int totalAmount;
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;
    private String cardNo;
    private String cardCorp;
    private String installMonth;
    @Enumerated(EnumType.STRING)
    private PaymentStatus status;
    @Enumerated(EnumType.STRING)
    private PaymentProvider paymentProvider;
    private Integer taxFree;
    private Integer vat;

    public Payment(KakaopayApproveResponse response) {
        this.orderId = Long.valueOf(response.getPartnerOrderId());
        this.tid = response.getTid();
        this.approvalId = response.getAid();
        this.totalAmount = response.getAmount().getTotal();
        this.paymentMethod = response.getPaymentMethodType()
            .equals("CARD") ? PaymentMethod.CARD : PaymentMethod.BANK;
        this.cardNo = null;
        this.cardCorp = getCardInfoField(response, CardInfo::getKakaopayPurchaseCorp);
        this.installMonth = getCardInfoField(response, CardInfo::getInstallMonth);
        this.status = PaymentStatus.COMPLETED;
        this.paymentProvider = PaymentProvider.KAKAO;
        this.taxFree =
            response.getAmount().getTaxFree() == null ? 0 : response.getAmount().getTaxFree();
        this.vat = response.getAmount().getVat();
    }

    @Builder
    public Payment(Long orderId, String tid, String approvalId, int totalAmount,
        PaymentMethod paymentMethod, String cardNo, String cardCorp, String installMonth,
        PaymentStatus status, PaymentProvider paymentProvider, Integer taxFree, Integer vat) {
        this.orderId = orderId;
        this.tid = tid;
        this.approvalId = approvalId;
        this.totalAmount = totalAmount;
        this.paymentMethod = paymentMethod;
        this.cardNo = cardNo;
        this.cardCorp = cardCorp;
        this.installMonth = installMonth;
        this.status = status;
        this.paymentProvider = paymentProvider;
        this.taxFree = taxFree;
        this.vat = vat;
    }

    public void updateStatus(PaymentStatus status) {
        this.status = status;
    }

    private String getCardInfoField(KakaopayApproveResponse response,
        Function<CardInfo, String> getter) {
        return Optional.ofNullable(response.getCardInfo())
            .map(getter)
            .orElse(null);
    }
}
