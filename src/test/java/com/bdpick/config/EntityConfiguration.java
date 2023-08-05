package com.bdpick.config;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.reactive.stage.Stage;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class EntityConfiguration {
    @Bean
//    @Primary
    Stage.SessionFactory factory() {
        return Persistence.createEntityManagerFactory("mariadb").unwrap(Stage.SessionFactory.class);

    }

    @Bean
    EntityManagerFactory getEntityfactory() {
        return Persistence.createEntityManagerFactory("mariadb");
    }


}