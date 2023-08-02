package com.bdpick.domain.entity.advertisement;

import com.bdpick.domain.entity.Keyword;
import com.bdpick.domain.entity.common.CreatedDate;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.apache.commons.lang3.builder.EqualsExclude;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;

/**
 * 광고 키워드 엔티티
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(uniqueConstraints = @UniqueConstraint(name = "UNIQUE_AD_KEYWORD", columnNames = {"ad_id", "keyword_id"}))
public class AdKeyword extends CreatedDate implements Serializable {
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, name = "ad_id", foreignKey = @ForeignKey(name = "FK_AD_KEYWORD_SHOP_AD_ID"))
    @Comment("홍보 아이디")
    @ToString.Exclude
    @EqualsExclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonBackReference
    private ShopAd shopAd;

    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "FK_AD_KEYWORD_KEYWORD_ID"))
    @Comment("키워드 아이디")
    @ToString.Exclude
    @OnDelete(action = OnDeleteAction.CASCADE)
    private Keyword keyword;

}
