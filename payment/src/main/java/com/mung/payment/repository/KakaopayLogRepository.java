package com.mung.payment.repository;

import com.mung.payment.domain.PaymentKakaoLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface KakaopayLogRepository extends JpaRepository<PaymentKakaoLog, String> {

}
