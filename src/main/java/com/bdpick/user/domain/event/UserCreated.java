package com.bdpick.user.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 회원 생성 됨
 */
@Getter
@Setter
@AllArgsConstructor
public class UserCreated {
    private String userId;
}
