package com.bdpick.domain.entity.advertisement;

import com.bdpick.domain.AdFileType;
import com.bdpick.domain.entity.common.CreatedDate;
import com.bdpick.domain.entity.common.Image;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

/**
 * ad image entity class
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class AdImage extends CreatedDate implements Serializable {

    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_AD_IMAGE_SHOP_AD_ID"))
    @ToString.Exclude
    @Comment("홍보 아이디")
    private ShopAd shopAd;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("광고 타입 A1,A2,A3,A4,A5")
    private AdFileType type;

    @Embedded
    @AssociationOverride(name = "bdFile", foreignKey = @ForeignKey(name = "FK_AD_IMAGE_FILE_ID"),
            joinColumns = @JoinColumn(name = "file_id"))
    private Image image;

}
