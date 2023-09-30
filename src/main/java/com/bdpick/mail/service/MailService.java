package com.bdpick.mail.service;

import java.util.List;

/**
 * mail service interface
 */
public interface MailService {
    boolean sendMail(List<String> to, String subject, String text);
}