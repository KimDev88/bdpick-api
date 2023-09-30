package com.bdpick.advertisement.domain;

import com.bdpick.common.domain.AuditDate;
import com.bdpick.shop.domain.Shop;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 매장 광고 엔티티
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class ShopAd extends AuditDate implements Serializable {
    @ManyToOne
    @OnDelete(action = OnDeleteAction.SET_NULL)
    @JoinColumn(nullable = false, foreignKey = @ForeignKey(name = "FK_SHOP_AD_SHOP_ID"))
    @Comment("가게 아이디")
    private Shop shop;

    @Column(nullable = false)
    @Comment("지점명")
    private String branchName;

    @Column(nullable = false)
    @Comment("시작일")
    private LocalDateTime startedAt;

    @Column(nullable = false)
    @Comment("종료일")
    private LocalDateTime endedAt;

    @Column(nullable = false, length = 2000)
    @Comment("홍보 내용")
    private String content;

    @OneToMany(mappedBy = "shopAd", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Comment("키워드 목록")
    @JsonManagedReference
    private List<AdKeyword> keywordList;

    @JsonManagedReference
    @OneToMany(mappedBy = "shopAd", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    private List<AdImage> adImageList;

}
