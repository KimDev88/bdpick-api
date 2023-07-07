package com.bdpick.common;

import io.vertx.core.Vertx;
import io.vertx.ext.mail.MailClient;
import io.vertx.ext.mail.MailConfig;
import io.vertx.ext.mail.MailMessage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MailService {
    @Value("${mail.host}")
    private String mailHost;
    @Value("${mail.username}")
    private String mailUserName;
    @Value("${mail.password}")
    private String mailPassword;

    public boolean sendMail(List<String> to, String subject, String text) {
        MailConfig mailConfig = new MailConfig();
        mailConfig.setHostname(mailHost)
                .setUsername(mailUserName)
                .setPassword(mailPassword);

        MailMessage message = new MailMessage();
        message.setSubject(subject);
        message.setFrom("no-reply@bdpick.com");
        message.setTo(to);
        message.setText(text);

        Vertx vertx = Vertx.vertx();
        MailClient mailClient = MailClient.create(vertx, mailConfig);
        return mailClient.sendMail(message).succeeded();

    }
}
