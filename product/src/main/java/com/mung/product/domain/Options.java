package com.mung.product.domain;

import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Table(uniqueConstraints = {@UniqueConstraint(
        name = "OPTIONS_UNIQUE",
        columnNames = {"PRODUCT_ID", "NAME"} )})
public class Options {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    private Integer price;

    public void setProduct(Product product) {
        this.product = product;
        product.getOptions().add(this);
    }

    @Builder
    public Options(Long id, String name, Integer price) {
        this.id = id;
        this.name = name;
        this.price = price;
    }
}
