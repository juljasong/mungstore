package com.mung.member.request;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSearchCondition {

    private Long memberId;
    private String email;
    private String tel;
    private String role;

    private String sortBy;
    private String sortDirection;

    private int pageNumber;
    private int pageSize;

}
