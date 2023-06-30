package com.bdpick.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Table("SHOP")
public class Shop implements Serializable, Persistable<Long> {

    @Id
    private Long id;
    private String userId;
    private String registNumber;
    private String name;
    private String ownerName;
    private String type;
    private String tel;
    private Long addressId;
    private String addressName;
    @Transient
    private String addressFullName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Transient
    private boolean isNew = false;

    @Override
    public boolean isNew() {
        return isNew;
    }
}
