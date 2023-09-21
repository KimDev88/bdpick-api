package com.bdpick.user.domain.event;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 이메일 검증 됨
 */
@Getter
@Setter
@AllArgsConstructor
public class MailVerified {
    private String email;
}
