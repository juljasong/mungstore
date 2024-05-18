package com.mung.member.repository;

import com.mung.member.domain.ResetPasswordUuid;
import org.springframework.data.repository.CrudRepository;

public interface ResetPasswordUuidRedisRepository extends CrudRepository<ResetPasswordUuid, String> {

}
