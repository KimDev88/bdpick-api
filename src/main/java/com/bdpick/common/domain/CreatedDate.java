package com.bdpick.common.domain;

import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 공통 등록일 엔티티
 */
@Getter
@Setter
@MappedSuperclass
public abstract class CreatedDate extends PrimaryKey implements Serializable {
    @Column(nullable = false)
    @CreationTimestamp
    @Comment("등록일")

    @JsonSerialize(using = LocalDateTimeSerializer.class)
    private LocalDateTime createdAt;
}
