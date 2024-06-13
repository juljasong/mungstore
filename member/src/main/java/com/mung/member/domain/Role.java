package com.mung.member.domain;

import lombok.Getter;

@Getter
public enum Role {

    USER("ROLE_USER"),
    COMP("ROLE_COMP"),
    ADMIN("ROLE_ADMIN");

    private String full;

    Role(String full) {
        this.full = full;
    }

}
