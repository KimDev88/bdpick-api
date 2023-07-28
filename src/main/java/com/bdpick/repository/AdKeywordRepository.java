package com.bdpick.repository;

import com.bdpick.domain.entity.Keyword;
import com.bdpick.domain.entity.advertisement.AdKeyword;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class AdKeywordRepository {
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
