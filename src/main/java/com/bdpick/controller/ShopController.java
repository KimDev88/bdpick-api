package com.bdpick.controller;

import com.bdpick.domain.entity.shop.Shop;
import com.bdpick.domain.request.CommonResponse;
import com.bdpick.service.ShopService;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.file.Path;
import java.util.Map;

import static com.bdpick.common.BdConstants.Exception.KEY_DUPLICATE_REGISTER;
import static com.bdpick.common.BdConstants.PREFIX_API_URL;

@Data
class Part {
    Path filePath;
    FilePart part;
}

/**
 * shop controller
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping(value = PREFIX_API_URL + "/shops")
//@RequestMapping(value = PREFIX_API_URL + "/shops", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
public class ShopController {


    @Value("${upload-path}")
    private String uploadPath;
    private final ShopService shopService;

    //    final private ShopRepository shopRepository;
//    final private ShopImageRepository shopImageRepository;
//    final private FileRepository fileRepository;
//    final JwtService jwtService;

    //    @GetMapping("this")
//    public Mono<CommonResponse> selectMyShop(@RequestHeader Map<String, Object> map) {
//        String token = BdUtil.getTokenByHeader(map);
//        String userId = jwtService.getUserIdByToken(token);
//        return shopRepository.findShopByUserId(userId)
//                .map(CommonResponse::new);
//    }
//
//
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


    //
//    @PostMapping(value = "fileUpload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
//    public Mono<CommonResponse> fileUpload(@RequestPart(value = "file", required = false) Mono<FilePart> filePart, @RequestPart(value = "directory", required = false) String fileType) {
//        CommonResponse response = new CommonResponse();
//        String url = "http://152.69.231.150/images/";
//
//        return filePart
//                .publishOn(Schedulers.boundedElastic())
//                .handle((part, sink) -> {
//                    String fileName = part.filename();
//                    Path path = Path.of(uploadPath, fileType);
//                    if (!Files.exists(path)) {
//                        try {
//                            Files.createDirectories(path);
//
//                        } catch (IOException e) {
//                            sink.error(new RuntimeException(e));
//                            return;
//                        }
//                    }
//                    Path resolve = path.resolve(part.filename());
//                    part.transferTo(resolve).subscribe();
//                    response.setData(url + fileType + "/" + fileName);
//                    sink.next(response);
//                });
//    }
//
    @Transactional
    @PostMapping
    public Mono<CommonResponse> createShop(@RequestHeader Map<String, Object> headerMap,
                                           @RequestPart("files") Flux<FilePart> files,
                                           @RequestPart("fileTypes") Flux<String> filesTypes,
                                           @RequestPart("shop") Shop shop) {
//
//        String userId = jwtService.getUserIdByHeaderMap(headerMap);
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