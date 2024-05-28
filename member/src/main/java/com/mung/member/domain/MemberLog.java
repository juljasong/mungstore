package com.mung.member.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MemberLog {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    private Long memberId;

    private String email;

    private String password;

    private String name;

    private String tel;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Embedded
    private Address address;

    private int loginFailCount;

    private boolean isLocked;

    private Long createdBy;

    private Long lastModifiedBy;

    private LocalDateTime createdAt;

    private LocalDateTime lastModifiedAt;

    @Builder
    public MemberLog(Long memberId, String email, String password, String name, String tel, Role role, Address address, int loginFailCount, boolean isLocked, Long createdBy, Long lastModifiedBy, LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        this.memberId = memberId;
        this.email = email;
        this.password = password;
        this.name = name;
        this.tel = tel;
        this.role = role;
        this.address = address;
        this.loginFailCount = loginFailCount;
        this.isLocked = isLocked;
        this.createdBy = createdBy;
        this.lastModifiedBy = lastModifiedBy;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
    }
}
