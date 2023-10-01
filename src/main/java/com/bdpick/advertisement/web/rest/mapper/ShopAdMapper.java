package com.bdpick.advertisement.web.rest.mapper;

import com.bdpick.advertisement.domain.AdKeyword;
import com.bdpick.advertisement.domain.ShopAd;
import com.bdpick.advertisement.web.rest.dto.ShopAdDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ShopAdMapper {
    ShopAdMapper INSTANCE = Mappers.getMapper(ShopAdMapper.class);

    @Mapping(target = "keywordList", ignore = true)
    ShopAdDto adToDto(ShopAd shopAd);

    List<ShopAdDto> adToDto(List<ShopAd> shopAd);

    //    @Mapping(target = "keywordList.keyword.keyword", source = "keywordList")
    ShopAd DtoToAd(ShopAdDto shopAdDto);

//    @Mapping(source = "keyword", target = ".")
//    String map2(AdKeyword value);

    @Mapping(source = "value", target = "keyword.keyword")
    AdKeyword map2(String value);


}
