package com.bdpick.controller;

import com.bdpick.domain.entity.shop.Shop;
import com.bdpick.domain.request.CommonResponse;
import com.bdpick.service.ShopService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.bdpick.common.BdConstants.Exception.KEY_DUPLICATE_REGISTER;
import static com.bdpick.common.BdConstants.PREFIX_API_URL;


/**
 * shop controller
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = PREFIX_API_URL + "/shops")
public class ShopController {
    private final ShopService shopService;

    /**
     * select my shop
     *
     * @param headerMap request headerMap
     * @return my shop
     */
    @GetMapping("this")
    public Mono<CommonResponse> selectMyShop(@RequestHeader Map<String, Object> headerMap) {
        CommonResponse response = new CommonResponse();
        return shopService.selectMyShop(headerMap)
                .map(response::setData)
                .onErrorResume(throwable -> {
                    log.error("throwable = ", throwable);
                    return Mono.just(response.setError().setMessage(throwable.getMessage()));
                });
    }

    /**
     * check register number is available
     *
     * @param shop shop
     * @return true : available, false : unavailable
     */
    @PostMapping(value = "check-register")
    public Mono<CommonResponse> checkRegisterNumber(@RequestBody Shop shop) {
        CommonResponse response = new CommonResponse();
        return shopService.checkRegisterNumber(shop)
                .onErrorResume(throwable -> {
                    log.error("throwable = ", throwable);
                    return Mono.just(response.setError().setMessage(throwable.getMessage()));
                });
    }

    /**
     * create shop
     *
     * @param headerMap  headerMap
     * @param files      files
     * @param filesTypes filesTypes
     * @param shop       shop
     * @return created shop
     */
    @Transactional
    @PostMapping
    public Mono<CommonResponse> createShop(@RequestHeader Map<String, Object> headerMap,
                                           @RequestPart("files") Flux<FilePart> files,
                                           @RequestPart("fileTypes") Flux<String> filesTypes,
                                           @RequestPart("shop") Shop shop) {
        CommonResponse response = new CommonResponse();

        return shopService.createShop(headerMap, files, filesTypes, shop)
                .map(CommonResponse::new)
                .onErrorResume(throwable -> {
                    log.error("throwable = ", throwable);
                    // 중복 사업자 번호가 존재할 경우
                    if (throwable instanceof RuntimeException) {
                        if (throwable.getCause().getMessage().equals(KEY_DUPLICATE_REGISTER)) {
                            return Mono.just(response.setError("해당 사업자 번호는 이미 가입되어있습니다.", null));
                        }
                    } else return Mono.just(response.setError().setMessage(throwable.getMessage()));
                    return Mono.just(response.setError().setMessage(throwable.getMessage()));
                });

    }
}