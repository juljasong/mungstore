package com.mung.payment.repository;

import com.mung.payment.domain.PaymentKakaoLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentKaKaoLogRepository extends JpaRepository<PaymentKakaoLog, String>, PaymentKaKaoLogRepositoryCustom {

}
