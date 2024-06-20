package com.mung.payment.repository;

import com.mung.payment.domain.KakaopayPayment;
import org.springframework.data.repository.CrudRepository;

public interface KakaopayPaymentRedisRepository extends CrudRepository<KakaopayPayment, Long> {

}
