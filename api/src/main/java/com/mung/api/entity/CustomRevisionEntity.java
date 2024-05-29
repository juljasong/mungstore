package com.mung.api.entity;

import com.mung.api.config.CustomRevisionEntityListener;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.envers.RevisionEntity;
import org.hibernate.envers.RevisionNumber;
import org.hibernate.envers.RevisionTimestamp;

import java.time.LocalDateTime;

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

    @Column @RevisionTimestamp
    private Long updatedAt;

    @Column @Setter
    private Long memberId;

    public LocalDateTime getUpdatedAt() {
        return LocalDateTime.now();
    }

}
