package com.mung.payment.repository;

import com.mung.payment.domain.PaymentKakaoLog;

public interface PaymentKaKaoLogRepositoryCustom {
    void nonDuplicatedSave(PaymentKakaoLog paymentKakaoLog);
}
