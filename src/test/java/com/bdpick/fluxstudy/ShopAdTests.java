package com.bdpick.fluxstudy;

import com.bdpick.controller.ShopAdController;
import com.bdpick.domain.entity.advertisement.ShopAd;
import com.bdpick.domain.request.CommonResponse;
import com.bdpick.service.ShopAdService;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import reactor.test.StepVerifier;

@RunWith(SpringRunner.class)
//@WebFluxTest(ShopAdController.class)
@SpringBootTest
public class ShopAdTests {
    @Autowired
    private ShopAdService shopAdService;

//    public ShopAdTests(ShopAdService shopAdService) {
//        this.shopAdService = shopAdService;
//    }

    @Test
    public void createShopAdTest() {
        ShopAd shopAd = new ShopAd();
        CommonResponse commonResponse = new CommonResponse();
        StepVerifier.create(shopAdService.createShopAd(shopAd))
                .expectNext(commonResponse)
                .verifyComplete();


    }
}
