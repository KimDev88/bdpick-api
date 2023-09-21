package com.bdpick.advertisement.repository.impl;

import com.bdpick.advertisement.domain.AdImage;
import com.bdpick.advertisement.repository.AdImageRepository;
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
public class AdImageRepositoryImpl implements AdImageRepository {
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
