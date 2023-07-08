package com.bdpick.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

@Mapper
public interface ShopAdMapper {
    ShopAdMapper INSTANCE = Mappers.getMapper(ShopAdMapper.class);

}
