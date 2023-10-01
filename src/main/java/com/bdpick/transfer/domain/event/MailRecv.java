package com.bdpick.transfer.domain.event;

import com.bdpick.user.domain.enumeration.EmailType;
import lombok.Getter;

/**
 * 메일 발송됨
 */
@Getter
public class MailRecv {
    private String userId;
    private String email;
    private EmailType type;
    private String subject;
    private String text;
}
