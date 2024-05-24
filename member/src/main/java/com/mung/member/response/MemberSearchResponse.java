package com.mung.member.response;

import com.mung.member.domain.Address;
import com.mung.member.domain.Role;
import com.querydsl.core.annotations.QueryProjection;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberSearchResponse {

    private Long memberId;
    private String name;
    private String email;
    private String tel;
    private Role role;
    private Address address;
    private int loginFailCount;
    private boolean isLocked;

    @QueryProjection
    public MemberSearchResponse(Long memberId, String name, String email, String tel, Role role, Address address, int loginFailCount, boolean isLocked) {
        this.memberId = memberId;
        this.name = name;
        this.email = email;
        this.tel = tel;
        this.role = role;
        this.address = address;
        this.loginFailCount = loginFailCount;
        this.isLocked = isLocked;
    }
}
