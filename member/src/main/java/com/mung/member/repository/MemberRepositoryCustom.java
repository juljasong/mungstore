package com.mung.member.repository;

import com.mung.member.request.MemberSearchCondition;
import com.mung.member.response.MemberSearchResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface MemberRepositoryCustom {
    Page<MemberSearchResponse> search(MemberSearchCondition memberSearchCondition, Pageable pageable);
}
