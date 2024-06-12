package com.mung.order.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.mung.common.domain.BaseEntity;
import com.mung.common.domain.BaseTimeEntity;
import com.mung.member.domain.Member;
import com.mung.product.domain.Options;
import com.mung.product.domain.Product;
import com.mung.stock.domain.Stock;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.hibernate.envers.Audited;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Audited
@AuditOverrides(value = {
    @AuditOverride(forClass = BaseEntity.class),
    @AuditOverride(forClass = BaseTimeEntity.class)
})
public class OrderItem extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @Setter
    @ManyToOne(fetch = LAZY)
    @JoinColumn(name = "order_id")
    private Orders order;

    private Long productId;

//    @ManyToOne(fetch = LAZY)
//    @JoinColumn(name = "product_id")
//    private Product product;

    private Long stockId;

//    @ManyToOne(fetch = LAZY)
//    @JoinColumn(name = "stock_id")
//    private Stock stock;

    private Long optionId;

    private Long memberId;

    private int orderPrice;

    private int quantity;

    private String contents;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Builder
    public OrderItem(Orders order, Long productId, Long stockId, Long optionId, Long memberId,
        int orderPrice, int quantity, String contents, OrderStatus status) {
        this.order = order;
        this.productId = productId;
        this.stockId = stockId;
        this.optionId = optionId;
        this.memberId = memberId;
        this.orderPrice = orderPrice;
        this.quantity = quantity;
        this.contents = contents;
        this.status = status;
    }

    public int getTotalPrice() {
        return getOrderPrice() * getQuantity();
    }
}
