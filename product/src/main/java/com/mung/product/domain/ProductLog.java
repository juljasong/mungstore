package com.mung.product.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductLog {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "log_id")
    private Long id;

    private Long product_id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String details;

    private Long compId;

    @Column(nullable = false)
    private Boolean activeForSale;

    @Builder
    public ProductLog(Long product_id, String name, String details, Long compId, boolean activeForSale) {
        this.product_id = product_id;
        this.name = name;
        this.details = details;
        this.compId = compId;
        this.activeForSale = activeForSale;
    }
}
