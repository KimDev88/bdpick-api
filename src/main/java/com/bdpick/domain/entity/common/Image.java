package com.bdpick.domain.entity.common;

import com.bdpick.domain.entity.BdFile;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import jakarta.persistence.ManyToOne;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 파일을 포함하는 이미지 공통 값 클래스
 */
@Getter
@Setter
@Embeddable
public class Image {
    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Comment("파일 아이디")
    private BdFile bdFile;

    @Column(nullable = false)
    @ColumnDefault("1")
    @Comment("표시 순서")
    private double displayOrder;
}
