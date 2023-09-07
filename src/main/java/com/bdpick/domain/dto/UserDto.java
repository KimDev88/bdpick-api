package com.bdpick.domain.dto;

import com.bdpick.domain.entity.User;
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
