package com.bdpick.user.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원 변경 됨
 */
@Getter
@Setter
@AllArgsConstructor
public class UserChanged {
    private String userId;
}
