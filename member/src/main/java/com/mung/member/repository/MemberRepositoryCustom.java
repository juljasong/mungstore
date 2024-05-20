package com.mung.member.repository;

import com.mung.member.request.MemberSearchCondition;
import com.mung.member.response.MemberSearch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {
    Page<MemberSearch> search(MemberSearchCondition memberSearchCondition, Pageable pageable);
}
