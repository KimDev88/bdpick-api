package com.bdpick.common.domain;

import com.bdpick.common.domain.BdFile;
import jakarta.persistence.*;
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
    @ManyToOne(fetch = FetchType.EAGER, optional = false, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @Comment("파일 아이디")
    private BdFile bdFile;

    @Column(nullable = false)
    @ColumnDefault("1")
    @Comment("표시 순서")
    private double displayOrder;
}
