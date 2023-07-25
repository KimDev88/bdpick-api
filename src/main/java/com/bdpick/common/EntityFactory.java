package com.bdpick.common;

import jakarta.persistence.Persistence;
import org.hibernate.reactive.stage.Stage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EntityFactory {
    @Bean
    Stage.SessionFactory getEntityManager() {
        return Persistence.createEntityManagerFactory("mariadb").unwrap(Stage.SessionFactory.class);
    }

}
