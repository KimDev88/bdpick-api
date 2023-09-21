package com.bdpick.user.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 로그인됨
 */
@Getter
@Setter
@AllArgsConstructor
public class LogIn {
    private String userId;
}
