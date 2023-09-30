package com.bdpick.user.web.rest.dto;

import com.bdpick.user.domain.User;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * User Dto class
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class UserDto extends User {
    private String uuid;
}
