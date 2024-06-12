package com.mung.order.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.mung.common.domain.BaseEntity;
import com.mung.common.domain.BaseTimeEntity;
import com.mung.member.domain.Member;
import com.mung.order.exception.AlreadyCancelledException;
import com.mung.order.exception.AlreadyDeliveredException;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import java.util.ArrayList;
import java.util.List;
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
public class Orders extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Long id;

    @ManyToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "member_id")
    private Member member;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private final List<OrderItem> orderItems = new ArrayList<>();

    @OneToOne(fetch = LAZY, cascade = CascadeType.ALL)
    @JoinColumn(name = "delivery_id")
    private Delivery delivery;

    @Enumerated(EnumType.STRING)
    private OrderStatus status;

    @Setter
    private int totalPrice;

    @Builder
    public Orders(Member member, Delivery delivery, OrderStatus status, int totalPrice) {
        this.member = member;
        this.delivery = delivery;
        this.status = status;
        this.totalPrice = totalPrice;
    }

    public static Orders createOrder(Member member, Delivery delivery, List<OrderItem> orderItems) {
        Orders order = Orders.builder()
            .member(member)
            .delivery(delivery)
            .status(OrderStatus.PAYMENT_PENDING)
            .build();

        for (OrderItem orderItem : orderItems) {
            order.addOrderItem(orderItem);
        }
        order.setTotalPrice(order.getTotalPrice());

        return order;
    }

    public void addOrderItem(OrderItem orderItem) {
        orderItems.add(orderItem);
        orderItem.setOrder(this);
    }

    public void setDelivery(Delivery delivery) {
        this.delivery = delivery;
        delivery.setOrder(this);
    }

    public void cancel() {
        if (delivery.getStatus() == DeliveryStatus.DELIVERED
            || delivery.getStatus() == DeliveryStatus.SHIPPED) {
            throw new AlreadyDeliveredException();
        }
        if (this.status == OrderStatus.CANCELLED) {
            throw new AlreadyCancelledException();
        }

        this.status = OrderStatus.CANCELLED;
        this.delivery.updateStatus(DeliveryStatus.CANCELLED);
        this.orderItems.forEach(item -> {
            item.updateStatus(OrderStatus.CANCELLED);
            item.getStock().addStock(item.getQuantity());
        });
    }

    public int getTotalPrice() {
        return orderItems.stream()
            .mapToInt(OrderItem::getTotalPrice)
            .sum();
    }

}
