package com.bdpick.domain;

import com.bdpick.repository.FileRepository;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.domain.Persistable;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.io.Serializable;
import java.time.LocalDateTime;

@Data
@Table("\"FILE\"")
public class BdFile implements Serializable, Persistable<Long> {

    @Id
    private Long id;
    private String oriName;
    private String destName;
    private String extension;
    private String uri;
    @Column("\"SIZE\"")
    private int size;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    @Transient
    private String fileType;
    @Transient
    private boolean isNew = false;

    @Override
    public boolean isNew() {
        return isNew;
    }
}
