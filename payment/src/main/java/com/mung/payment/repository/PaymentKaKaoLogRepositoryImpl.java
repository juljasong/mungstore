package com.mung.payment.repository;

import com.mung.common.exception.DuplicateKeyException;
import com.mung.payment.domain.PaymentKakaoLog;
import jakarta.persistence.EntityManager;
import org.springframework.beans.factory.annotation.Autowired;

public class PaymentKaKaoLogRepositoryImpl implements PaymentKaKaoLogRepositoryCustom {

    @Autowired
    EntityManager em;

    @Override
    public void nonDuplicatedSave(PaymentKakaoLog paymentKakaoLog) {
        if (em.find(PaymentKakaoLog.class, paymentKakaoLog.getAid()) != null) {
            throw new DuplicateKeyException();
        } else {
            em.persist(paymentKakaoLog);
        }
    }
}
