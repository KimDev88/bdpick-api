package com.bdpick.user.web.rest.dto;

import com.bdpick.user.domain.enumeration.UserType;
import lombok.Data;

/**
 * signIn Dto class
 */
@Data
public class SignInDto {
    private Token token;
    private UserType userType;
}
