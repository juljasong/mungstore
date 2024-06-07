package com.mung.api.entity;

import com.mung.api.config.CustomRevisionEntityListener;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

@Getter
@Entity
@Table(name = "RevisionHistory")
@RevisionEntity(CustomRevisionEntityListener.class)
public class CustomRevisionEntity {

    @Id
    @Column
    @RevisionNumber
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long revisionId;

    @Column
    @RevisionTimestamp
    private Long updatedAt;

    @Column
    @Setter
    private Long memberId;

    public LocalDateTime getUpdatedAt() {
        return LocalDateTime.now();
    }

}
