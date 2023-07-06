package com.bdpick.domain.advertisement;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Table("SHOP_AD")
public class ShopAd implements Serializable, Persistable<Long> {
    @Id
    private Long id;
    private Long shopId;
    private String branchName;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Transient
    private List<String> keywordList;
    @Transient
    private boolean isNew = false;

    @Override
    public boolean isNew() {
        return isNew;
    }

}
