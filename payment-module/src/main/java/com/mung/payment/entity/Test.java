package com.mung.payment.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.Id;

@Entity
public class Test {

    @Id @GeneratedValue
    private Long id;
}
