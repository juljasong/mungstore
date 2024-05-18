package com.mung.member.repository;

import com.mung.member.domain.AccessToken;
import org.springframework.data.repository.CrudRepository;

public interface AccessTokenRedisRepository extends CrudRepository<AccessToken, Long> {
}
