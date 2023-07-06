package com.bdpick.domain.advertisement;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Table("AD_KEYWORD")
public class AdKeyword implements Serializable, Persistable<Long> {
    @Id
    private Long id;
    private Long adId;
    private Long keywordId;
    private LocalDateTime createdAt;
    @Transient
    private boolean isNew = false;

    @Override
    public boolean isNew() {
        return isNew;
    }

}
