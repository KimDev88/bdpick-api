package com.bdpick.repository;

import com.bdpick.domain.entity.BdFile;
import lombok.RequiredArgsConstructor;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

import static io.smallrye.mutiny.converters.uni.BuiltinConverters.toCompletableFuture;

/**
 * BdFile Repository
 */
@Repository
@RequiredArgsConstructor
public class FileRepository {
    private final Stage.SessionFactory factory;

    /**
     * save bdFile
     *
     * @param bdFile entity
     * @return saved entity
     */
    public CompletionStage<BdFile> save(BdFile bdFile, Stage.Session session) {
        session.persist(bdFile);
        return CompletableFuture.completedStage(bdFile);
    }
}
