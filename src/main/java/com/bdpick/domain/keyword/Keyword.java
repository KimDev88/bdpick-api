package com.bdpick.domain.keyword;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Table("KEYWORD")
public class Keyword implements Serializable, Persistable<Long> {
    @Id
    private Long id;
    private String keyword;
    private LocalDateTime createdAt;
    @Transient
    private boolean isNew = false;

    @Override
    public boolean isNew() {
        return isNew;
    }

}
