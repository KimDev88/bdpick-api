package com.bdpick.user.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 이메일 변경됨
 */
@Getter
@Setter
@AllArgsConstructor
public class MailChanged {
    private String userId;
    private String email;

}
