package com.mung.common.domain;

import lombok.Getter;

@Getter
public enum Role {

    USER("ROLE_USER"),
    COMP("ROLE_COMP"),
    ADMIN("ROLE_ADMIN");

    private String full;

    private Role(String full) {
        this.full = full;
    }

}
