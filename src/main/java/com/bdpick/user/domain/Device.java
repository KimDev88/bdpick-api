package com.bdpick.user.domain;

import com.bdpick.common.domain.AuditDate;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

import java.io.Serializable;

/**
 * device entity class
 */
@Entity
@Data
@Table(uniqueConstraints = @UniqueConstraint(name = "UNQ_DEVICE_USER_ID", columnNames = {"user_id"}))
@EqualsAndHashCode(callSuper = true)
public class Device extends AuditDate implements Serializable {
    @ManyToOne(fetch = FetchType.EAGER)
    @OnDelete(action = OnDeleteAction.CASCADE)
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
