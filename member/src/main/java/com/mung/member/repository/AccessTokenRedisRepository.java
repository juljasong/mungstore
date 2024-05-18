package com.mung.member.repository;

import com.mung.member.domain.AccessToken;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface AccessTokenRedisRepository extends CrudRepository<AccessToken, Long> {
    Optional<AccessToken> findByAccessToken(String accessToken);
}
