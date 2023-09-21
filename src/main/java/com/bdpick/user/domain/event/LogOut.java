package com.bdpick.user.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 패스워드 변경됨
 */
@Getter
@Setter
@AllArgsConstructor
public class LogOut {
    private String userId;
}
