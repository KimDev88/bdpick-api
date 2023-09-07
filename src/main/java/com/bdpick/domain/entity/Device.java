package com.bdpick.domain.entity;

import com.bdpick.domain.entity.common.AuditDate;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

/**
 * device entity class
 */
@Entity
@Data
@EqualsAndHashCode(callSuper = true)
public class Device extends AuditDate implements Serializable {
    @OneToOne(fetch = FetchType.EAGER)
    @JoinColumn(foreignKey = @ForeignKey(name = "FK_DEVICE_USER_ID"), nullable = false)
    private User user;

    @Column(nullable = false, length = 36)
    @Comment("UUID")
    private String uuid;

    @Column(nullable = false)
    @Comment("푸시토큰")
    private String pushToken;

    @Column(nullable = false)
    @Comment("리프레시토큰")
    private String refreshToken;
}
