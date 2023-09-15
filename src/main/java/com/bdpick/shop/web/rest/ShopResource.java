package com.bdpick.shop.web.rest;

import com.bdpick.domain.request.CommonResponse;
import com.bdpick.shop.domain.Shop;
import com.bdpick.shop.service.impl.ShopService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

import static com.bdpick.common.BdConstants.Exception.KEY_DUPLICATE_REGISTER;
import static com.bdpick.common.BdConstants.Exception.MSG_DUPLICATE_REGISTER;
import static com.bdpick.common.BdConstants.PREFIX_API_URL;


/**
 * shop resource
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = PREFIX_API_URL + "/shops")
public class ShopResource {
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
                    response.setError().setMessage(throwable.getMessage());

                    // 중복 사업자 번호가 존재할 경우
                    if (throwable instanceof RuntimeException) {
                        if (throwable.getCause() != null &&
                                throwable.getCause().getMessage().equals(KEY_DUPLICATE_REGISTER)) {
                            response.setError(MSG_DUPLICATE_REGISTER, null);
                        }
                    }
                    return Mono.just(response);
                });

    }
}