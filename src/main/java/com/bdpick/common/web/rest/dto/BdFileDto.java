package com.bdpick.common.web.rest.dto;

import com.bdpick.common.domain.BdFile;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class BdFileDto extends BdFile {
    private String fileType;
}
