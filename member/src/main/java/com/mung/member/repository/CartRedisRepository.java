package com.mung.member.repository;

import com.mung.member.domain.Cart;
import org.springframework.data.repository.CrudRepository;

public interface CartRedisRepository extends CrudRepository<Cart, Long> {

}
