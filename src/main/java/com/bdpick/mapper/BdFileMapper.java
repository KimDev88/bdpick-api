package com.bdpick.mapper;

import com.bdpick.domain.dto.BdFileDto;
import com.bdpick.domain.entity.BdFile;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface BdFileMapper {
    BdFileMapper INSTANCE = Mappers.getMapper(BdFileMapper.class);

    BdFile BdFileDtoToBdfile(BdFileDto bdFileDto);
}
