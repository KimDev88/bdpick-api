package com.bdpick.mapper;

import com.bdpick.domain.dto.UserDto;
import com.bdpick.domain.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * user entity to dto mapper
 */
@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto userToDto(User user);


}
