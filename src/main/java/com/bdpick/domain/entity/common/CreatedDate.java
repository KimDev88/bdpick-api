package com.bdpick.domain.entity.common;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

/**
 * 공통 등록일 엔티티
 */
@Getter
@Setter
@MappedSuperclass
public abstract class CreatedDate extends PrimaryKey {
    @Column(nullable = false)
    @CreationTimestamp
    @Comment("등록일")
    private LocalDateTime createdAt;
}
