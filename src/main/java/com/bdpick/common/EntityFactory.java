package com.bdpick.common;

import jakarta.persistence.EntityManagerFactory;
import jakarta.persistence.Persistence;
import org.hibernate.reactive.mutiny.Mutiny;
import org.hibernate.reactive.stage.Stage;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * jpa 사용을 위한 entity manager factory bean
 */
@Configuration
public class EntityFactory {
    @Bean
    Stage.SessionFactory getEntitySessionManager() {
        return Persistence.createEntityManagerFactory("mariadb").unwrap(Stage.SessionFactory.class);
    }

    @Bean
    Mutiny.SessionFactory getMutinySessionManager() {
        return Persistence.createEntityManagerFactory("mariadb").unwrap(Mutiny.SessionFactory.class);
    }

    @Bean
    EntityManagerFactory getSessionFactory(){
        return Persistence.createEntityManagerFactory("mariadb");
    }


}
