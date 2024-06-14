package com.mung.product.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.mung.stock.domain.Stock;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Getter
@Audited
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {
    @UniqueConstraint(name = "OPTIONS_UNIQUE", columnNames = {"PRODUCT_ID", "NAME"})})
public class Options {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;

    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false)
    private String name;

    private Integer price;

    private Boolean available;

    @OneToOne(fetch = LAZY)
    @JoinColumn(name = "option_id")
    @NotAudited
    private Stock stock;

    @Builder
    public Options(String name, Integer price, Boolean available) {
        this.name = name;
        this.price = price;
        this.available = available;
    }

    public void setProduct(Product product) {
        this.product = product;
        product.getOptions().add(this);
    }
}
