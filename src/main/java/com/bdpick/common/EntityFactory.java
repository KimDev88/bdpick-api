package com.bdpick.common;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.cfg.AvailableSettings;
import org.hibernate.reactive.mutiny.Mutiny;
import org.hibernate.reactive.stage.Stage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.HashMap;
import java.util.Map;

/**
 * jpa 사용을 위한 entity manager factory bean
 */
@Configuration
public class EntityFactory {
    @Value("${jdbc.url}")
    private String jdbcUrl;
    @Value("${jdbc.user}")
    private String jdbcUser;
    @Value("${jdbc.password}")
    private String jdbcPassword;
    Map<String, String> propertiesMap = new HashMap<>();

    @Bean
    Stage.SessionFactory getEntitySessionManager() {
        return getFactoryWithProperties().unwrap(Stage.SessionFactory.class);
    }

    @Bean
    Mutiny.SessionFactory getMutinySessionManager() {
        return getFactoryWithProperties().unwrap(Mutiny.SessionFactory.class);
    }

    /**
     * get hibernate factory with properties
     *
     * @return EntityManagerFactory
     */
    EntityManagerFactory getFactoryWithProperties() {
        propertiesMap.put(AvailableSettings.JAKARTA_JDBC_URL, jdbcUrl);
        propertiesMap.put(AvailableSettings.JAKARTA_JDBC_USER, jdbcUser);
        propertiesMap.put(AvailableSettings.JAKARTA_JDBC_PASSWORD, jdbcPassword);
        return Persistence.createEntityManagerFactory("mariadb", propertiesMap);
    }


}

