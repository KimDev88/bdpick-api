package com.bdpick.user.web.rest.mapper;

import com.bdpick.user.web.rest.dto.UserDto;
import com.bdpick.user.domain.User;
import org.mapstruct.Mapper;
import org.mapstruct.factory.Mappers;

/**
 * user entity to dto mapper
 */
@Mapper
public interface UserMapper {
    UserMapper INSTANCE = Mappers.getMapper(UserMapper.class);

    UserDto userToDto(User user, String uuid);
    User DtoToUser(UserDto dto);


}
