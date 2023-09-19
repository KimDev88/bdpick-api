package com.bdpick.user.domain;

import com.bdpick.domain.entity.common.CreatedDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.hibernate.annotations.Comment;

import java.io.Serializable;

@Data
@Entity
@EqualsAndHashCode(callSuper = true)
public class Verify extends CreatedDate implements Serializable {
    @Column(nullable = false)
    @Comment("이메일")
    private String email;

    @Column(length = 6, nullable = false)
    @Comment("인증코드")
    private String code;
}


