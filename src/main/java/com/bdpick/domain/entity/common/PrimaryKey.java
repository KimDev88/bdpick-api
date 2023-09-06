package com.bdpick.domain.entity.common;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

/**
 * 공통 생성 아이디 엔티티
 */
@Getter
@Setter
@MappedSuperclass
public abstract class PrimaryKey {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("아이디")
    private Long id;
}
