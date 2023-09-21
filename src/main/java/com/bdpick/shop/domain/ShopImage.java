package com.bdpick.shop.domain;

import com.bdpick.shop.domain.enumeration.ShopFileType;
import com.bdpick.domain.entity.common.CreatedDate;
import com.bdpick.domain.entity.common.Image;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

/**
 * ship image entity class
 */
@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class ShopImage extends CreatedDate implements Serializable {
    @ManyToOne(optional = false)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_SHOP_IMAGE_SHOP_ID"))
    @ToString.Exclude
    @JsonBackReference
    @Comment("가게 아이디")
    private Shop shop;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Comment("가게 이미지 타입 S1,S2,S3,S4")
    private ShopFileType type;

    @Embedded
    @AssociationOverride(name = "bdFile", foreignKey = @ForeignKey(name = "FK_SHOP_IMAGE_FILE_ID"), joinColumns = @JoinColumn(name = "file_id"))
    private Image image;

}
