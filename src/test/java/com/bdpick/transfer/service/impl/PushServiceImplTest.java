package com.bdpick.transfer.service.impl;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

/**
 * push service implement test
 */
class PushServiceImplTest {
    String testToken1;
    String testToken2;
    String topic;

    @BeforeEach
    void setUp() {
        testToken1 = "ed01d8tRQXyfUTwN7nAKpK:APA91bHjI6V3cMeXioTkEkpXNGPa-xJd6iXtMRDMeZ3nkefMI-SNheNM9SveEFdTBaJd04kwWShm9KCCc2bBoSiGdMWusdKFvyo9wPjHiOD4dP-DtO4mK7BMpkWF9mvfoYm2IYJvNIdC";
        testToken2 = "fNblpIZHS2yS4oepkkmd7t:APA91bF_pEiQfRbRv7Gl8DStsG_w4lQbzUQAyO-n1z8XeGG-mvXKJdgQTpg9aEDbmTawhRfar6ZlgUN4mmkSrqsBe5_2DDcRWL2aY6MctWP88m0RYel0DJWmqJGPucrF6_pZSyRO07Ki";
        topic = "test";
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void sendByTokens() {
    }

    @Test
    void sendWithTokens() {
    }

    @Test
    void sendByTopics() {
    }

    @Test
    void sendByCondition() {
    }

    @Test
    void subscribeToTopic() {
    }

    @Test
    void unsubscribeToTopic() {
    }
}