package com.mung.member.request;

import com.mung.common.request.BaseSearchRequest;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSearchCondition extends BaseSearchRequest{

    private Long memberId;
    private String email;
    private String tel;
    private String role;

}
