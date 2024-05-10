package com.mung.common.domain;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Member {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
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

    @Builder
    public Member(String email, String password, String name, String tel, Role role, Address address) {
        this.email = email;
        this.password = password;
        this.name = name;
        this.tel = tel;
        this.role = role;
        this.address = address;
    }

}
