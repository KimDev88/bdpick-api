//package com.bdpick.domain;
//
//import lombok.Data;
//import org.springframework.data.annotation.CreatedDate;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.annotation.Transient;
//import org.springframework.data.domain.Persistable;
//import org.springframework.data.relational.core.mapping.Table;
//
//import java.io.Serializable;
//import java.time.LocalDateTime;
//
//@Data
//@Table("DEVICE")
//public class Device implements Serializable, Persistable<Long> {
//
//    @Id
//    private Long id;
//    private String userId;
//    private String uuid;
//    private String pushToken;
//    private String refreshToken;
//    @CreatedDate
//    private LocalDateTime createdAt;
//    @Transient
//    private boolean isNew = false;
//
//    @Override
//    public boolean isNew() {
//        return isNew;
//    }
//}
