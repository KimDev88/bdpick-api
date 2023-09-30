package com.bdpick.user.adaptor;

import com.bdpick.user.domain.enumeration.EmailType;
import com.fasterxml.jackson.core.JsonProcessingException;

/**
 * user event producer class
 */
public interface UserProducer {
    /**
     * found id
     *
     * @param email 발송할 이메일
     */
    void findId(String email) throws JsonProcessingException;

    /**
     * verify id
     *
     * @param userId userId
     */
    void verifyId(String userId) throws JsonProcessingException;

    /**
     * log in
     *
     * @param userId userId
     */
    void logIn(String userId) throws JsonProcessingException;

    /**
     * log out
     *
     * @param userId userId
     */
    void logOut(String userId) throws JsonProcessingException;

    /**
     * change mail
     *
     * @param userId userId
     * @param email  changed email
     */
    void changeMail(String userId, String email) throws JsonProcessingException;

    /**
     * send mail
     *
     * @param userId userId
     * @param email  email
     * @param type   email type
     */
    void sendMail(String userId, String email, EmailType type, String subject, String text) throws JsonProcessingException;

    /**
     * verify email
     *
     * @param email email
     */
    void verifyMail(String email) throws JsonProcessingException;

    /**
     * create user
     *
     * @param userId userId
     */
    void createUser(String userId) throws JsonProcessingException;

    /**
     * change user
     *
     * @param userId userId
     */
    void changeUser(String userId) throws JsonProcessingException;
}
