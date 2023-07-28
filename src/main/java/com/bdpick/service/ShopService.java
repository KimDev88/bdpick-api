package com.bdpick.service;

import com.bdpick.domain.entity.Shop;
import com.bdpick.domain.request.CommonResponse;
import com.bdpick.repository.ShopRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class ShopService {
    private final ShopRepository shopRepository;

    /**
     * create shop
     *
     * @param shop entity
     * @return created entity
     */
    public Mono<CommonResponse> createShop(Shop shop) {
        CommonResponse commonResponse = new CommonResponse();
        return Mono.just(commonResponse.setData(shopRepository.save(shop)));
    }
}
