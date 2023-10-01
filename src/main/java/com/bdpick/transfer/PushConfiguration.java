package com.bdpick.transfer;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.FileInputStream;
import java.io.IOException;

/**
 * push configuration class
 */
@Configuration
public class PushConfiguration {
    @Value("${config.path}")
    private String configPath;

    @Bean
    @Qualifier("push")
    public void init() throws IOException {
        FileInputStream fileInputStream = new FileInputStream(configPath + "/firebase-key.json");
        FirebaseOptions options = FirebaseOptions.builder()
                .setCredentials(GoogleCredentials.fromStream(fileInputStream))
                .build();
        FirebaseApp.initializeApp(options);

    }
}
