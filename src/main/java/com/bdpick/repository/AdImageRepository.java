package com.bdpick.repository;

import com.bdpick.domain.entity.advertisement.AdImage;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * AdImage Repository
 */
@Repository
@RequiredArgsConstructor
public class AdImageRepository {
    private final Stage.SessionFactory factory;

    /**
     * save adImage
     *
     * @param adImage entity
     * @return saved entity
     */
    public CompletionStage<AdImage> save(AdImage adImage, Stage.Session session) {
        session.persist(adImage);
        return CompletableFuture.completedStage(adImage);
    }
}
