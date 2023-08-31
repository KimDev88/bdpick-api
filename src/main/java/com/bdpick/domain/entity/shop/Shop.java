package com.bdpick.domain.entity.shop;

import com.bdpick.domain.entity.User;
import com.bdpick.domain.entity.common.AuditDate;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Entity
@Data
@Table(uniqueConstraints =
        {
                @UniqueConstraint(name = "UNQ_SHOP_USER_ID", columnNames = {"user_id"}),
                @UniqueConstraint(name = "UNQ_SHOP_REGISTER_NUMBER", columnNames = {"registerNumber"})
        }
)
@EqualsAndHashCode(callSuper = true)
public class Shop extends AuditDate implements Serializable {
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", foreignKey = @ForeignKey(name = "FK_SHOP_USER_ID"))
    @ToString.Exclude
    private User user;

    @Column(nullable = false, length = 10, columnDefinition = "CHAR(10)")
    @Comment("사업자 등록번호")
    private String registerNumber;

    @Column(nullable = false, length = 100)
    @Comment("상호명")
    private String name;

    @Column(nullable = false, length = 100)
    @Comment("대표자명")
    private String ownerName;

    @Column(nullable = true, length = 6)
    @Comment("가게유형")
    private String type;

    @Column(nullable = false, length = 11, columnDefinition = "CHAR(11)")
    @Comment("전화번호")
    private String tel;

    @Comment("주소아이디")
    private Long addressId;

    @Column(nullable = false)
    @Comment("상세주소명")
    private String addressName;

    @ToString.Exclude
    @OneToMany(mappedBy = "shop", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @OnDelete(action = OnDeleteAction.CASCADE)
    @JsonManagedReference
    private List<ShopImage> imageList = new ArrayList<>();
//    @Transient
//    private String addressFullName;
}