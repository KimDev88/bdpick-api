package com.bdpick.domain.entity;

import com.bdpick.domain.UserType;
import com.bdpick.domain.entity.shop.Shop;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import lombok.Data;
import org.hibernate.annotations.Comment;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "UNQ_EMAIL", columnNames = {"email"}))
//public class User extends AuditDate implements Serializable, UserDetails {
public class User implements Serializable {
    @Id
    @Column(length = 100)
    @Comment("회원 아이디")
    private String id;

    @Column(nullable = false)
    @Comment("회원 패스워드 암호화")
    private String password;

    @Column(nullable = false)
    @Comment("회원 이메일")
    private String email;

    @Enumerated(EnumType.STRING)
    @Comment("N:일반유저 O : 사업주")
    private UserType type;

    @CreationTimestamp
    @Comment("등록일")
    private LocalDateTime createdAt;

    @Column(nullable = false)
    @UpdateTimestamp
    @Comment("수정일")
    private LocalDateTime updatedAt;

    @Transient
    private String uuid;

    @JsonBackReference
    @OneToOne(mappedBy = "user")
    private Shop shop;


//
//    @Override
//    public Collection<? extends GrantedAuthority> getAuthorities() {
//        return null;
//    }
//
//    @Override
//    public String getUsername() {
//        return null;
//    }
//
//    @Override
//    public boolean isAccountNonExpired() {
//        return false;
//    }
//
//    @Override
//    public boolean isAccountNonLocked() {
//        return false;
//    }
//
//    @Override
//    public boolean isCredentialsNonExpired() {
//        return false;
//    }
//
//    @Override
//    public boolean isEnabled() {
//        return false;
//    }
}
