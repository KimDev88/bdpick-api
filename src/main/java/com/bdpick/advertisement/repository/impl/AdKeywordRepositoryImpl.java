package com.bdpick.advertisement.repository.impl;

import com.bdpick.advertisement.domain.AdKeyword;
import com.bdpick.advertisement.repository.AdKeywordRepository;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class AdKeywordRepositoryImpl implements AdKeywordRepository {
    private final Stage.SessionFactory factory;

    /**
     * create keyword
     *
     * @param adKeyword entity
     * @return saved entity
     */
    public Mono<AdKeyword> save(AdKeyword adKeyword) {
        factory.withTransaction((session, transaction) ->
                session.persist(adKeyword)
        ).toCompletableFuture().join();
        return Mono.just(adKeyword);
    }

}
