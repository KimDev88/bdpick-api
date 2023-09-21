package com.bdpick.user.adaptor;

import com.bdpick.user.domain.enumeration.EmailType;
import com.bdpick.user.domain.event.*;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

/**
 * shop event producer implement class
 */
@RequiredArgsConstructor
@Service
public class UserProducerImpl implements UserProducer {
    private final KafkaTemplate<String, String> kafkaTemplate;
    private static final String TOPIC_FIND_ID = "topic_find_id";
    private static final String TOPIC_VERIFY_ID = "topic_verify_id";
    private static final String TOPIC_LOG_IN = "topic_log_in";
    private static final String TOPIC_LOG_OUT = "topic_log_out";
    private static final String TOPIC_CHANGE_MAIL = "topic_change_mail";
    private static final String TOPIC_SEND_MAIL = "topic_send_mail";
    private static final String TOPIC_VERIFY_MAIL = "topic_verify_mail";
    private static final String TOPIC_CREATE_USER = "topic_create_user";
    private static final String TOPIC_CHANGE_USER = "topic_change_user";

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * found id
     *
     * @param email 발송할 이메일
     */
    @Override
    public void findId(String email) throws JsonProcessingException {
        IdFound idFound = new IdFound(email);
        String message = objectMapper.writeValueAsString(idFound);
        kafkaTemplate.send(TOPIC_FIND_ID, message);

    }

    /**
     * verify id
     *
     * @param userId userId
     */
    @Override
    public void verifyId(String userId) throws JsonProcessingException {
        IdVerified idVerified = new IdVerified(userId);
        String message = objectMapper.writeValueAsString(idVerified);
        kafkaTemplate.send(TOPIC_VERIFY_ID, message);
    }

    /**
     * log in
     *
     * @param userId userId
     */
    @Override
    public void logIn(String userId) throws JsonProcessingException {
        LogIn logIn = new LogIn(userId);
        String message = objectMapper.writeValueAsString(logIn);
        kafkaTemplate.send(TOPIC_LOG_IN, message);
    }

    /**
     * log out
     *
     * @param userId userId
     */
    @Override
    public void logOut(String userId) throws JsonProcessingException {
        LogOut logOut = new LogOut(userId);
        String message = objectMapper.writeValueAsString(logOut);
        kafkaTemplate.send(TOPIC_LOG_OUT, message);
    }


    /**
     * change mail
     *
     * @param userId userId
     * @param email  changed email
     */
    @Override
    public void changeMail(String userId, String email) throws JsonProcessingException {
        MailChanged mailChanged = new MailChanged(userId, email);
        String message = objectMapper.writeValueAsString(mailChanged);
        kafkaTemplate.send(TOPIC_CHANGE_MAIL, message);
    }

    /**
     * send mail
     *
     * @param userId userId
     * @param email  email
     * @param type   email type
     */
    @Override
    public void sendMail(String userId, String email, EmailType type) throws JsonProcessingException {
        MailSent mailSent = new MailSent(userId, email, type);
        String message = objectMapper.writeValueAsString(mailSent);
        kafkaTemplate.send(TOPIC_SEND_MAIL, message);
    }

    /**
     * verify email
     *
     * @param email email
     */
    @Override
    public void verifyMail(String email) throws JsonProcessingException {
        MailVerified mailVerified = new MailVerified(email);
        String message = objectMapper.writeValueAsString(mailVerified);
        kafkaTemplate.send(TOPIC_VERIFY_MAIL, message);
    }


    /**
     * create user
     *
     * @param userId userId
     */
    @Override
    public void createUser(String userId) throws JsonProcessingException {
        UserCreated userCreated = new UserCreated(userId);
        String message = objectMapper.writeValueAsString(userCreated);
        kafkaTemplate.send(TOPIC_CREATE_USER, message);
    }

    /**
     * change user
     *
     * @param userId userId
     */
    @Override
    public void changeUser(String userId) throws JsonProcessingException {
        UserChanged userChanged = new UserChanged(userId);
        String message = objectMapper.writeValueAsString(userChanged);
        kafkaTemplate.send(TOPIC_CHANGE_USER, message);
    }
}
