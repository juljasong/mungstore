package com.mung.order.domain;

import static jakarta.persistence.FetchType.LAZY;

import com.mung.common.domain.Address;
import com.mung.common.domain.BaseEntity;
import com.mung.common.domain.BaseTimeEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Embedded;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.envers.AuditOverride;
import org.hibernate.envers.AuditOverrides;
import org.hibernate.envers.Audited;
import org.hibernate.envers.NotAudited;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Audited
@AuditOverrides(value = {
    @AuditOverride(forClass = BaseEntity.class),
    @AuditOverride(forClass = BaseTimeEntity.class)
})
public class Delivery extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long id;

    private String shipper;

    private String trackingNo;

    private String tel1;

    private String tel2;

    @Embedded
    private Address address;

    @Enumerated(EnumType.STRING)
    private DeliveryStatus status;

    @Setter
    @NotAudited
    @OneToOne(mappedBy = "delivery", fetch = LAZY)
    private Orders order;

    @Builder
    public Delivery(String shipper, String trackingNo, String tel1, String tel2, Address address,
        DeliveryStatus status) {
        this.shipper = shipper;
        this.trackingNo = trackingNo;
        this.tel1 = tel1;
        this.tel2 = tel2;
        this.address = address;
        this.status = status;
    }
}
