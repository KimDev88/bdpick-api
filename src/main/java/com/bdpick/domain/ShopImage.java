//package com.bdpick.domain;
//
//import lombok.Data;
//import org.springframework.data.annotation.Id;
//import org.springframework.data.annotation.Transient;
//import org.springframework.data.domain.Persistable;
//import org.springframework.data.relational.core.mapping.Table;
//
//import java.io.Serializable;
//import java.time.LocalDateTime;
//
//@Data
//@Table("SHOP_IMAGE")
//public class ShopImage implements Serializable, Persistable<Long> {
//    @Id
//    private Long id;
//    private ShopFileType type;
//    private Long shopId;
//    private Long fileId;
//    private double displayOrder;
//    private LocalDateTime createdAt;
//    @Transient
//    private boolean isNew = false;
//
//    @Override
//    public boolean isNew() {
//        return isNew;
//    }
//
//
//
//
//}
