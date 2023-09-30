package com.bdpick.common.web.rest.mapper;

import com.bdpick.common.web.rest.dto.BdFileDto;
import com.bdpick.common.domain.BdFile;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BdFileMapper {
    BdFileMapper INSTANCE = Mappers.getMapper(BdFileMapper.class);

    BdFile BdFileDtoToBdfile(BdFileDto bdFileDto);
}
