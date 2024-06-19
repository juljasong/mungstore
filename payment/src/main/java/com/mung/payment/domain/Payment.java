package com.mung.payment.domain;

import com.mung.common.domain.BaseEntity;
import com.mung.common.domain.PaymentMethod;
import com.mung.common.domain.PaymentProvider;
import com.mung.common.domain.PaymentStatus;
import com.mung.payment.dto.PaymentDto.KaKaoCompletePaymentRequest;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
    private int taxFree;
    private int vat;

    public Payment(KaKaoCompletePaymentRequest request) {
        this.orderId = Long.valueOf(request.getPartnerOrderId());
        this.tid = request.getTid();
        this.approvalId = request.getAid();
        this.totalAmount = request.getAmount().getTotal();
        this.paymentMethod = request.getPaymentMethodType()
            .equals("CARD") ? PaymentMethod.CARD : PaymentMethod.BANK;
        this.cardNo = null;
        this.cardCorp = request.getCardInfo().getKakaopayPurchaseCorp();
        this.installMonth = request.getCardInfo().getInstallMonth();
        this.status = PaymentStatus.COMPLETED;
        this.paymentProvider = PaymentProvider.KAKAO;
        this.taxFree =
            request.getAmount().getTaxFree() != null ? request.getAmount().getTaxFree() : 0;
        this.vat = request.getAmount().getVat() != null ? request.getAmount().getVat() : 0;
    }

    @Builder
    public Payment(Long orderId, String tid, String approvalId, int totalAmount,
        PaymentMethod paymentMethod, String cardNo, String cardCorp, String installMonth,
        PaymentStatus status, PaymentProvider paymentProvider, int taxFree, int vat) {
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
}
