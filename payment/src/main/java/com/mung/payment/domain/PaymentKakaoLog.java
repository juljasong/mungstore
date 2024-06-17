package com.mung.payment.domain;

import com.mung.payment.dto.PaymentDto.KaKaoCompletePaymentRequest;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PaymentKakaoLog {

    @Id
    private String aid;
    private String tid;
    private String cid;
    private String partnerUserId;
    private String partnerOrderId;  // 주문번호
    private String paymentMethodType;
    private Integer total;
    private Integer taxFree;
    private Integer vat;
    private Integer point;
    private Integer discount;
    private Integer greenDeposit;
    private String kakaopayPurchaseCorp;
    private String kakaopayPurchaseCorpCode;
    private String kakaopayIssuerCorp;
    private String kakaopayIssuerCorpCode;
    private String bin;
    private String cardType;
    private String installMonth;
    private String approvedId;
    private String cardMid;
    private String interestFreeInstall;
    private String installmentType;
    private String cardItemCode;
    private String itemName;
    private String itemCode;
    private Integer quantity;
    private Date createdAt;
    private Date approvedAt;
    private String payload;

    public PaymentKakaoLog(KaKaoCompletePaymentRequest request) {
        this.aid = request.getAid();
        this.tid = request.getTid();
        this.cid = request.getCid();
        this.partnerUserId = request.getPartnerUserId();
        this.partnerOrderId = request.getPartnerOrderId();
        this.paymentMethodType = request.getPaymentMethodType();
        this.total = request.getAmount().getTotal();
        this.taxFree = request.getAmount().getTaxFree();
        this.vat = request.getAmount().getVat();
        this.point = request.getAmount().getPoint();
        this.discount = request.getAmount().getDiscount();
        this.greenDeposit = request.getAmount().getGreenDeposit();
        this.kakaopayPurchaseCorp = request.getCardInfo().getKakaopayPurchaseCorp();
        this.kakaopayPurchaseCorpCode = request.getCardInfo().getKakaopayPurchaseCorpCode();
        this.kakaopayIssuerCorp = request.getCardInfo().getKakaopayIssuerCorp();
        this.kakaopayIssuerCorpCode = request.getCardInfo().getKakaopayIssuerCorpCode();
        this.bin = request.getCardInfo().getBin();
        this.cardType = request.getCardInfo().getCardType();
        this.installMonth = request.getCardInfo().getInstallMonth();
        this.approvedId = request.getCardInfo().getApprovedId();
        this.cardMid = request.getCardInfo().getCardMid();
        this.interestFreeInstall = request.getCardInfo().getInterestFreeInstall();
        this.installmentType = request.getCardInfo().getInstallmentType();
        this.cardItemCode = request.getCardInfo().getCardItemCode();
        this.itemName = request.getItemName();
        this.itemCode = request.getItemCode();
        this.quantity = request.getQuantity();
        this.createdAt = request.getCreatedAt();
        this.approvedAt = request.getApprovedAt();
        this.payload = request.getPayload();
    }
}
