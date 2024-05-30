package com.mung.product.domain;

import com.mung.common.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@ToString
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "category_id")
    private Long id;

    @Column(nullable = false)
    private String name;

    @Setter
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    @OneToMany(mappedBy = "parent")
    private final List<Category> children = new ArrayList<>();

    @OneToMany(mappedBy = "category")
    private final List<Product> products = new ArrayList<>();

    @Column(name = "depth")
    private int depth;

    @Builder
    public Category(String name, int depth) {
        this.name = name;
        this.depth = depth;
    }

    public void addChildCategory(Category child) {
        this.children.add(child);
        child.setParent(this);
    }
}
