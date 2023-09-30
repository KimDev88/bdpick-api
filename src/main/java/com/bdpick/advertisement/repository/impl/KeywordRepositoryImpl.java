package com.bdpick.advertisement.repository.impl;

import com.bdpick.advertisement.repository.KeywordRepository;
import com.bdpick.advertisement.domain.Keyword;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class KeywordRepositoryImpl implements KeywordRepository {
    private final Stage.SessionFactory factory;

    /**
     * create keyword
     *
     * @param keyword entity
     * @return saved entity
     */
    public Mono<Keyword> save(Keyword keyword) {
        factory.withTransaction((session, transaction) ->
                session.persist(keyword)).toCompletableFuture().join();
        return Mono.just(keyword);
    }

    //    public Mono<Keyword> findOrSave(Keyword keyword) {
    public Keyword findOrSave(Keyword keyword) {
        return factory.withSession((session) -> {
                    return session.createQuery("select k from Keyword k where k.keyword = :keyword", Keyword.class)
                            .setParameter("keyword", keyword.getKeyword())
                            .getSingleResultOrNull();
                })
                .thenApply(keyword1 -> {
                    if (keyword1 == null) {
                        keyword1 = keyword;
                    }
                    System.out.println("keyword1 = " + keyword1);
                    return keyword1;
                })
                .toCompletableFuture().join();
    }

}
