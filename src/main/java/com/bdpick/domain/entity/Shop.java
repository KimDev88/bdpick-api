package com.bdpick.domain.entity;//package com.bdpick.domain;

import com.bdpick.domain.entity.common.AuditDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.*;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Shop extends AuditDate implements Serializable {

    private String userId;

    @Column(nullable = false, length = 10, columnDefinition = "CHAR(10)")
    @Comment("사업자 등록번호")
    private String registNumber;

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
//    @Transient
//    private String addressFullName;
}
