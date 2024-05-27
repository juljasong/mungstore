package com.mung.product.domain;

import com.mung.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicInsert;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
public class Product extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "product_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private int price;

    @Column(nullable = false)
    private String details;

    private Long compId;

    @Column(nullable = false)
    private Boolean activeForSale;

    @Column(nullable = false)
    private Boolean useYn;

    @Column(nullable = false)
    @OneToMany(mappedBy = "product")
    private List<ProductCategory> categories = new ArrayList<>();

    @Column(nullable = false)
    @OneToMany(mappedBy = "product")
    private List<Options> options = new ArrayList<>();

//    @OneToMany(mappedBy = "product")
//    private List<Review> reviews = new ArrayList<>();

    public void setCategory(List<ProductCategory> list) {
        if (!list.isEmpty()) {
            this.categories.clear();
            this.categories.addAll(list);
        }
    }

    public void updateProduct(String name, int price, String details, Boolean activeForSale) {
        this.name = name;
        this.price = price;
        this.details = details;
        this.activeForSale = activeForSale;
    }

    public void deleteProduct(Long id) {
        this.useYn = false;
    }

    @Builder
    public Product(String name, int price, String details, Long compId, Boolean activeForSale) {
        this.name = name;
        this.price = price;
        this.details = details;
        this.compId = compId;
        this.activeForSale = activeForSale;
    }
}
