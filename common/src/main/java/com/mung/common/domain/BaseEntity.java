package com.mung.common.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class BaseEntity extends BaseTimeEntity {

    @Column(updatable = false)
    private String createdBy;

    private String lastModifiedBy;

}
