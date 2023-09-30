package com.bdpick.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Getter
@Setter
@MappedSuperclass
public abstract class AuditDate extends CreatedDate {
    @Column(nullable = false)
    @UpdateTimestamp
    @Comment("수정일")
    private LocalDateTime updatedAt;

}
