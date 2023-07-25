//package com.bdpick.repository;
//
//import com.bdpick.domain.BdFile;
//import org.springframework.data.domain.Example;
//import org.springframework.data.r2dbc.repository.Query;
//import org.springframework.data.repository.reactive.ReactiveCrudRepository;
//import org.springframework.stereotype.Repository;
//import reactor.core.publisher.Mono;
//
//@Repository
//public interface FileRepository extends ReactiveCrudRepository<BdFile, Long> {
//
////    @Query("INSERT INTO \"FILE\" (ID, ORI_NAME, DEST_NAME, EXTENSION, URI, \"SIZE\", CREATED_AT, UPDATED_AT)" +
////            " VALUES (SEQ_FILE.NEXTVAL, :#{#files.oriName}, :#{#files.destName}, :#{#files.extension}, :#{#files.uri}, :#{#files.size}, :#{#files.createdAt}, :#{#files.updatedAt})")
////    @Override
////    <S extends BdFile> Mono<S> save(S files);
//
//    @Query("SELECT SEQ_FILE.nextval FROM DUAL")
//    Mono<Long> getSequence();
//
//}
