package com.bdpick.domain.entity;

import com.bdpick.domain.FileType;
import com.bdpick.domain.entity.advertisement.ShopAd;
import com.bdpick.domain.entity.common.CreatedDate;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
//@Table("AD_IMAGE")
public class AdImage extends CreatedDate implements Serializable {
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("광고 타입 A1,A2,A3,A4,A5")
    private FileType type;

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_AD_IMAGE_SHOP_AD_ID"))
    @ToString.Exclude
    @Comment("홍보 아이디")
    private ShopAd shopAd;

    @ManyToOne(optional = false, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JoinColumn(name = "file_id", foreignKey = @ForeignKey(name = "FK_AD_IMAGE_FILE_ID"))
    @Comment("파일 아이디")
    private BdFile bdFile;

    @Column(nullable = false)
    @ColumnDefault("1")
    @Comment("표시 순서")
    private double displayOrder;


}
