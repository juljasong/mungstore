package com.mung.product.domain;

import com.mung.common.domain.BaseEntity;
import com.mung.common.domain.BaseTimeEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@DynamicInsert
@Audited
@AuditOverrides(value = {
        @AuditOverride(forClass = BaseEntity.class),
        @AuditOverride(forClass = BaseTimeEntity.class)
})
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

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    @NotAudited
    private Category category;

    @Column(nullable = false)
    @OneToMany(mappedBy = "product")
    @NotAudited
    private List<Options> options = new ArrayList<>();

//    @OneToMany(mappedBy = "product")
//    private List<Review> reviews = new ArrayList<>();

    public void setCategory(Category category) {
        this.category = category;
        category.getProducts().add(this);
    }

    public void updateProduct(String name, int price, String details, Boolean activeForSale, Category category) {
        this.name = name;
        this.price = price;
        this.details = details;
        this.activeForSale = activeForSale;
        this.category = category;
    }

    public void deleteProduct(Long id) {
        this.useYn = false;
    }

    @Builder
    public Product(String name, int price, String details, Long compId, Boolean activeForSale, Boolean useYn, Category category) {
        this.name = name;
        this.price = price;
        this.details = details;
        this.compId = compId;
        this.activeForSale = activeForSale;
        this.useYn = useYn;
        this.category = category;
    }
}
