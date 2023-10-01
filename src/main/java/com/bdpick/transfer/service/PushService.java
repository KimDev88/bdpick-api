package com.bdpick.transfer.service;

import com.google.firebase.messaging.FirebaseMessagingException;

import java.util.List;

/**
 * push notification service interface
 */
public interface PushService {
    /**
     * [tokenList]로 푸시를 보낸다.
     *
     * @param tokenList tokenList
     * @return 성공 횟수
     */
    int sendByTokens(List<String> tokenList) throws FirebaseMessagingException;

    /**
     * tokenList를 조회해서 푸시로 보낸다.
     *
     * @return 성공 횟수
     */
    int sendWithTokens();

    /**
     * 해당 topic 리스트의 구독자들에게 푸시를 보낸다.
     *
     * @param topicList tokenList
     * @return 성공횟수
     */
    int sendByTopics(List<String> topicList) throws FirebaseMessagingException;

    /**
     * 해당 조건으로 푸시를 보낸다.
     * <p>
     * e.g. "'TopicA' in topics && ('TopicB' in topics || 'TopicC' in topics)"
     *
     * @param condition 조건식
     * @return 성공 횟수
     */
    int sendByCondition(String condition) throws FirebaseMessagingException;

    /**
     * 해당 토큰 리스트로 주제를 구독한다.
     *
     * @param tokenList tokenList
     * @param topic     topic
     * @return 성공 횟수
     */
    int subscribeToTopic(List<String> tokenList, String topic) throws FirebaseMessagingException;

    /**
     * 해당 토큰 리스트로 주제 구독을 해지한다.
     *
     * @param tokenList tokenList
     * @param topic     topic
     * @return 성공 횟수
     */
    int unsubscribeToTopic(List<String> tokenList, String topic) throws FirebaseMessagingException;
}
