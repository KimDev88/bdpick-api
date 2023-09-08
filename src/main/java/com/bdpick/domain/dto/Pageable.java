package com.bdpick.domain.dto;

import lombok.Data;

/**
 * paging 처리를 위한 class
 */
@Data
public class Pageable {
    int offset = 0;
    int size = 20;

    public Pageable() {

    }

    public Pageable(int offset, int size) {
        this.offset = offset;
        this.size = size;
    }
}
