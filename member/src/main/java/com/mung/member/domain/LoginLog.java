package com.mung.member.domain;

import com.mung.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.Builder;

@Entity
public class LoginLog extends BaseTimeEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    @Column(nullable = false)
    private String email;

    @Column(nullable = false)
    private boolean isSuccess;


    @Builder
    public LoginLog(String email, boolean isSuccess) {
        this.email = email;
        this.isSuccess = isSuccess;
    }
}
