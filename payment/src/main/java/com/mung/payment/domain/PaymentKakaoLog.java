package com.mung.payment.domain;

import com.mung.common.domain.PaymentStatus;
import com.mung.payment.dto.KakaopayDto.KakaopayApproveResponse;
import com.mung.payment.dto.KakaopayDto.KakaopayApproveResponse.CardInfo;
import com.mung.payment.dto.KakaopayDto.KakaopayCancelResponse;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import java.util.Date;
import java.util.Optional;
import java.util.function.Function;
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
    private String status;
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

    public PaymentKakaoLog(KakaopayApproveResponse response) {
        this.aid = response.getAid();
        this.tid = response.getTid();
        this.cid = response.getCid();
        this.status = PaymentStatus.COMPLETED.name();
        this.partnerUserId = response.getPartnerUserId();
        this.partnerOrderId = response.getPartnerOrderId();
        this.paymentMethodType = response.getPaymentMethodType();
        this.total = response.getAmount().getTotal();
        this.taxFree = response.getAmount().getTaxFree();
        this.vat = response.getAmount().getVat();
        this.point = response.getAmount().getPoint();
        this.discount = response.getAmount().getDiscount();
        this.greenDeposit = response.getAmount().getGreenDeposit();
        this.kakaopayPurchaseCorp = getCardInfoField(response, CardInfo::getKakaopayPurchaseCorp);
        this.kakaopayPurchaseCorpCode = getCardInfoField(response, CardInfo::getKakaopayPurchaseCorpCode);
        this.kakaopayIssuerCorp = getCardInfoField(response, CardInfo::getKakaopayIssuerCorp);
        this.kakaopayIssuerCorpCode = getCardInfoField(response, CardInfo::getKakaopayIssuerCorpCode);
        this.bin = getCardInfoField(response, CardInfo::getBin);
        this.cardType = getCardInfoField(response, CardInfo::getCardType);
        this.installMonth = getCardInfoField(response, CardInfo::getInstallMonth);
        this.approvedId = getCardInfoField(response, CardInfo::getApprovedId);
        this.cardMid = getCardInfoField(response, CardInfo::getCardMid);
        this.interestFreeInstall = getCardInfoField(response, CardInfo::getInterestFreeInstall);
        this.installmentType = getCardInfoField(response, CardInfo::getInstallmentType);
        this.cardItemCode = getCardInfoField(response, CardInfo::getCardItemCode);
        this.itemName = response.getItemName();
        this.itemCode = response.getItemCode();
        this.quantity = response.getQuantity();
        this.createdAt = response.getCreatedAt();
        this.approvedAt = response.getApprovedAt();
        this.payload = response.getPayload();
    }

    public PaymentKakaoLog(KakaopayCancelResponse response) {
        this.aid = response.getAid();
        this.tid = response.getTid();
        this.cid = response.getCid();
        this.status = response.getStatus();
        this.partnerUserId = response.getPartnerUserId();
        this.partnerOrderId = response.getPartnerOrderId();
        this.paymentMethodType = response.getPaymentMethodType();
        this.total = response.getAmount().getTotal();
        this.taxFree = response.getAmount().getTaxFree();
        this.vat = response.getAmount().getVat();
        this.point = response.getAmount().getPoint();
        this.discount = response.getAmount().getDiscount();
        this.greenDeposit = response.getAmount().getGreenDeposit();
        this.itemName = response.getItemName();
        this.itemCode = response.getItemCode();
        this.quantity = response.getQuantity();
        this.createdAt = response.getCreatedAt();
        this.approvedAt = response.getApprovedAt();
        this.payload = response.getPayload();
    }

    private String getCardInfoField(KakaopayApproveResponse response, Function<CardInfo, String> getter) {
        return Optional.ofNullable(response.getCardInfo())
            .map(getter)
            .orElse(null);
    }
}
