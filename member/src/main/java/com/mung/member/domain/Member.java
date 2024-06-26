package com.mung.member.domain;

import com.mung.common.domain.Address;
import com.mung.common.domain.BaseEntity;
import com.mung.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.hibernate.envers.Audited;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Audited
@AuditOverrides(value = {
    @AuditOverride(forClass = BaseEntity.class),
    @AuditOverride(forClass = BaseTimeEntity.class)
})
public class Member extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "member_id")
    private Long id;

    @Column(unique = true)
    private String email;

    private String password;

    private String name;

    @Column(unique = true)
    private String tel;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Embedded
    private Address address;

    @ColumnDefault("0")
    private int loginFailCount;

    @ColumnDefault("false")
    private boolean isLocked;

    @Builder
    public Member(String email, String password, String name, String tel, Role role,
        Address address) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.tel = tel;
        this.role = role;
        this.address = address;
    }

    public int addLoginFailCount() {
        this.loginFailCount += 1;
        return this.loginFailCount;
    }

    public void resetLoginFailCount() {
        this.loginFailCount = 0;
    }

    public void lockAccount() {
        this.isLocked = true;
        this.password = "";
    }

    public void unlockAccount() {
        this.isLocked = false;
    }

    public void resetPassword(String password) {
        this.password = password;
        unlockAccount();
        resetLoginFailCount();
    }

    public void updateTel(String tel) {
        this.tel = tel;
    }

    public void updateAddress(Address address) {
        this.address = address;
    }

    public void updatePassword(String password) {
        this.password = password;
    }


}
