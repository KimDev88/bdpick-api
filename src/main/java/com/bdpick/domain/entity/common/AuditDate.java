package com.bdpick.domain.entity.common;

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
public class AuditDate extends CreatedDate {
    @Column(nullable = false)
    @UpdateTimestamp
    @Comment("수정일")
    private LocalDateTime updatedAt;

}
