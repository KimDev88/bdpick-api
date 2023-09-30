package com.bdpick.user.domain.event;

import com.bdpick.user.domain.enumeration.EmailType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 메일 발송됨
 */

@Getter
@Setter
@AllArgsConstructor
public class MailSent {
    private String userId;
    private String email;
    private EmailType type;
    private String subject;
    private String text;
}
