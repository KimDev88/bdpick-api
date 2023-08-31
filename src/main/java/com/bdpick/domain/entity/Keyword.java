package com.bdpick.domain.entity;

import com.bdpick.domain.entity.common.CreatedDate;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;


@Entity
@Data
@EqualsAndHashCode(callSuper = true)
@Table(uniqueConstraints = @UniqueConstraint(name = "UNQ_KEYWORD", columnNames = {"keyword"}))
public class Keyword extends CreatedDate implements Serializable {
    @Column(length = 10)
    private String keyword;
}
