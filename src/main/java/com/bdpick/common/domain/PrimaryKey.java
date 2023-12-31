package com.bdpick.common.domain;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

/**
 * 공통 생성 아이디 엔티티
 */
@Getter
@Setter
@MappedSuperclass
public abstract class PrimaryKey implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Comment("아이디")
    private Long id;
}
