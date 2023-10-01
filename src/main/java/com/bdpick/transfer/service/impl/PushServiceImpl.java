package com.bdpick.transfer.service.impl;

import com.bdpick.transfer.service.PushService;
import com.google.firebase.messaging.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * push service implement class
 */
@Service
@Slf4j
public class PushServiceImpl implements PushService {

    /**
     * [tokenList]로 푸시를 보낸다.
     *
     * @param tokenList tokenList
     * @return 성공 횟수
     */
    @Override
    public int sendByTokens(List<String> tokenList) throws FirebaseMessagingException {
        MulticastMessage message = MulticastMessage.builder()
                .putData("score", "850")
                .putData("time", "2:45")
                .addAllTokens(tokenList)
                .build();
        BatchResponse response = FirebaseMessaging.getInstance().sendEachForMulticast(message);

        // 에러 로깅 처리
        if (response.getFailureCount() > 0) {
            List<SendResponse> responses = response.getResponses();
            List<String> failedTokens = new ArrayList<>();
            for (int i = 0; i < responses.size(); i++) {
                if (!responses.get(i).isSuccessful()) {
                    // The order of responses corresponds to the order of the registration tokens.
                    failedTokens.add(tokenList.get(i));
                }
            }
            log.info("List of tokens that caused failures: " + failedTokens);
        }

        log.info(response.getSuccessCount() + " messages were sent successfully");
        return response.getSuccessCount();
    }

    /**
     * tokenList를 조회해서 푸시로 보낸다.
     *
     * @return 성공 횟수
     */
    @Override
    public int sendWithTokens() {
        // fixme 디비 조회 후 전송 로직 추가
        return 0;
    }

    /**
     * 해당 topic 리스트의 구독자들에게 푸시를 보낸다.
     *
     * @param topicList tokenList
     * @return 성공횟수
     */
    @Override
    public int sendByTopics(List<String> topicList) throws FirebaseMessagingException {
        List<Message> messages = new ArrayList<>();

        for (String s : topicList) {
            Message message = Message.builder()
                    .putData("score", "850")
                    .putData("time", "2:45")
                    .setTopic(s)
                    .build();
            messages.add(message);
        }

        BatchResponse response = FirebaseMessaging.getInstance().sendEach(messages);
        log.info("List of tokens that caused failures: " + response.getFailureCount());
        log.info(response.getSuccessCount() + " messages were sent successfully");
        return response.getSuccessCount();
    }

    /**
     * 해당 조건으로 푸시를 보낸다.
     * <p>
     * e.g. "'TopicA' in topics && ('TopicB' in topics || 'TopicC' in topics)"
     *
     * @param condition 조건식
     * @return 성공 횟수
     */
    @Override
    public int sendByCondition(String condition) throws FirebaseMessagingException {
        Message message = Message.builder()
                .setNotification(Notification.builder()
                        .setTitle("$GOOG up 1.43% on the day")
                        .setBody("$GOOG gained 11.80 points to close at 835.67, up 1.43% on the day.")
                        .build())
                .setCondition(condition)
                .build();

        String response = FirebaseMessaging.getInstance().send(message);
        System.out.println("Successfully sent message: " + response);
        return Integer.parseInt(response);
    }

    /**
     * 해당 토큰 리스트로 주제를 구독한다.
     *
     * @param tokenList tokenList
     * @param topic     topic
     * @return 성공 횟수
     */
    @Override
    public int subscribeToTopic(List<String> tokenList, String topic) throws FirebaseMessagingException {
        verifySubscribeMax(tokenList.size());
        TopicManagementResponse response = FirebaseMessaging.getInstance().subscribeToTopic(
                tokenList, topic);
        loggingSubscribeFailure(response);
        log.info(response.getSuccessCount() + " tokens were subscribed successfully");
        return response.getSuccessCount();
    }

    /**
     * 해당 토큰 리스트로 주제 구독을 해지한다.
     *
     * @param tokenList tokenList
     * @param topic     topic
     * @return 성공 횟수
     */
    @Override
    public int unsubscribeToTopic(List<String> tokenList, String topic) throws FirebaseMessagingException {
        verifySubscribeMax(tokenList.size());
        TopicManagementResponse response = FirebaseMessaging.getInstance().unsubscribeFromTopic(
                tokenList, topic);
        loggingSubscribeFailure(response);
        log.info(response.getSuccessCount() + " tokens were unsubscribed successfully");
        return response.getSuccessCount();
    }

    /**
     * subscribe 최대 개수 체크
     *
     * @param size item size
     */
    private void verifySubscribeMax(int size) {
        if (size > 1000) {
            log.error("token max size is 1000");
            throw new RuntimeException();
        }
    }

    /**
     * logging Subscribe Failure
     *
     * @param response response
     */
    private void loggingSubscribeFailure(TopicManagementResponse response) {
        // 에러 로깅 처리
        if (response.getFailureCount() > 0) {
            List<TopicManagementResponse.Error> responses = response.getErrors();
            for (TopicManagementResponse.Error error : responses) {
                log.error(error.getIndex() + " : " + error.getReason());
            }
            log.info("count of failures: " + response.getFailureCount());
        }

    }
}


