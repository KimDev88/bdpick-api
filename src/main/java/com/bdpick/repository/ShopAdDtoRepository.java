//package com.bdpick.repository;
//
//import com.bdpick.dto.ShopAdDto;
//import org.springframework.data.repository.reactive.ReactiveCrudRepository;
//import reactor.core.publisher.Flux;
//
//public interface ShopAdDtoRepository extends ReactiveCrudRepository<ShopAdDto, Long> {
//
//    Flux<ShopAdDto> findShopAdDtosByShopIdIsNotNullOrderByCreatedAtDesc();
//    Flux<ShopAdDto> findShopAdDtosByShopId(Long shopId);
//}