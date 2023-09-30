package com.bdpick.common.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Data
@Entity
@Table(name = "FILE")
@EqualsAndHashCode(callSuper = true)
public class BdFile extends AuditDate implements Serializable {

    @Column(nullable = false)
    @Comment("원본 파일명")
    private String oriName;

    @Column(nullable = false)
    @Comment("변환 파일명")
    private String destName;

    @Column(nullable = false, length = 5)
    @Comment("파일 확장자")
    private String extension;

    @Column(nullable = false)
    @Comment("파일 경로")
    private String uri;

    @Column(nullable = false)
    @Comment("파일 크기 (Byte)")
    private int size;
}
