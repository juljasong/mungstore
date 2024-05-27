package com.mung.product.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;

import java.time.LocalDateTime;

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
    private int price;

    @Column(nullable = false)
    private String details;

    private Long compId;

    private Boolean useYn;

    @Column(nullable = false)
    private Boolean activeForSale;

    @Column(updatable = false)
    private String createdBy;

    private String lastModifiedBy;

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime lastModifiedAt;

    @Builder
    public ProductLog(Long product_id, String name, int price, String details, Long compId, Boolean useYn, Boolean activeForSale, String createdBy, String lastModifiedBy, LocalDateTime createdAt, LocalDateTime lastModifiedAt) {
        this.product_id = product_id;
        this.name = name;
        this.price = price;
        this.details = details;
        this.compId = compId;
        this.useYn = useYn;
        this.activeForSale = activeForSale;
        this.createdBy = createdBy;
        this.lastModifiedBy = lastModifiedBy;
        this.createdAt = createdAt;
        this.lastModifiedAt = lastModifiedAt;
    }

}
