package com.mung.stock.domain;

import com.mung.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Stock extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stock_id")
    private Long id;

    @Column(name = "sku_id")
    private String skuId;

    private Long optionId;

    private int quantity;

    @Builder
    public Stock(String skuId, Long optionId, int quantity) {
        this.skuId = skuId;
        this.optionId = optionId;
        this.quantity = quantity;
    }

    public boolean inStock() {
        return this.quantity < 1;
    }
}
