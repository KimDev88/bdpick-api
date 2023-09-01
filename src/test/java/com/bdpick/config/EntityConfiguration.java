package com.bdpick.config;

import jakarta.persistence.Persistence;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.reactive.mutiny.Mutiny;
import org.hibernate.reactive.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

import java.util.HashMap;
import java.util.Map;

@TestConfiguration
public class EntityConfiguration {
    @Value("${jdbc.url}")
    private String jdbcUrl;
    @Value("${jdbc.user}")
    private String jdbcUser;
    @Value("${jdbc.password}")
    private String jdbcPassword;
    Map<String, String> propertiesMap = new HashMap<>();

    @Bean
    Stage.SessionFactory getEntitySessionManager() {
        propertiesMap.put(AvailableSettings.JAKARTA_JDBC_URL, jdbcUrl);
        propertiesMap.put(AvailableSettings.JAKARTA_JDBC_USER, jdbcUser);
        propertiesMap.put(AvailableSettings.JAKARTA_JDBC_PASSWORD, jdbcPassword);
        return Persistence.createEntityManagerFactory("mariadb", propertiesMap).unwrap(Stage.SessionFactory.class);
    }

    @Bean
    Mutiny.SessionFactory getMutinySessionManager() {
        propertiesMap.put(AvailableSettings.JAKARTA_JDBC_URL, jdbcUrl);
        propertiesMap.put(AvailableSettings.JAKARTA_JDBC_USER, jdbcUser);
        propertiesMap.put(AvailableSettings.JAKARTA_JDBC_PASSWORD, jdbcPassword);
        return Persistence.createEntityManagerFactory("mariadb", propertiesMap).unwrap(Mutiny.SessionFactory.class);
    }
}