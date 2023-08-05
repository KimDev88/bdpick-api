package com.bdpick.repository;


import com.bdpick.domain.entity.Verify;
import lombok.NonNull;
import org.hibernate.reactive.stage.Stage;
import org.springframework.stereotype.Repository;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionStage;

/**
 * verify repository class
 */
@Repository
public class VerifyRepository {

    /**
     * save verify
     *
     * @param verify  verify
     * @param session session
     * @return created verify
     */
    public CompletionStage<Verify> save(@NonNull Verify verify, @NonNull Stage.Session session) {
        session.persist(verify);
        return CompletableFuture.completedStage(verify);
    }

    /**
     * find last verify by email and
     *
     * @param verify  verify with email and code
     * @param session session
     * @return found verify
     */
    public CompletionStage<Verify> findLastByEmailAndCode(@NonNull Verify verify, @NonNull Stage.Session session) {
        return session.createQuery("select v from Verify v " +
                        "where email = :email and code = :code " +
                        "order by createdAt desc limit 1", Verify.class)
                .setParameter("email", verify.getEmail())
                .setParameter("code", verify.getCode())
                .getSingleResultOrNull();
    }

}
