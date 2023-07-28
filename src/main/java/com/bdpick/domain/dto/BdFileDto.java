package com.bdpick.domain.dto;

import com.bdpick.domain.entity.BdFile;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BdFileDto extends BdFile {
    private String fileType;
}
