package com.mung.common.repository;

import com.mung.common.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface LoginRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);
}
