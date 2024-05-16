package com.mung.member.repository;

import com.mung.member.domain.Member;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member, Long> {

    Optional<Member> findByEmail(String email);

    Optional<Member> findByTel(String tel);

    Optional<Member>  findByEmailAndTel(String email, String tel);
}
