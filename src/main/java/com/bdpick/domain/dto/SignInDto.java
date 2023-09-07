package com.bdpick.domain.dto;

import com.bdpick.domain.UserType;
import lombok.Data;

/**
 * signIn Dto class
 */
@Data
public class SignInDto {
    private Token token;
    private UserType userType;
}
