package com.bdpick.transfer.adaptor;

import com.bdpick.transfer.domain.event.MailRecv;
import com.bdpick.transfer.service.MailService;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MailConsumer {
    private final MailService mailService;
    public static final String TOPIC_MAIL = "topic_mail";
    @KafkaListener(topicPattern = TOPIC_MAIL)
    public void consume(String message) throws IOException {
        System.out.println("message = " + message);
        ObjectMapper mapper = new ObjectMapper();
        MailRecv mailRecv = mapper.readValue(message, MailRecv.class);
        mailService.sendMail(List.of(mailRecv.getEmail()), mailRecv.getSubject(), mailRecv.getText());
        switch (mailRecv.getType()){
            case MAIL_VERIFIED -> {}
        }
    }
}
