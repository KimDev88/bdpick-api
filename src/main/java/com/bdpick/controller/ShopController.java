//package com.bdpick.controller;
//
//import com.bdpick.common.BdUtil;
//import com.bdpick.common.security.JwtService;
//import com.bdpick.domain.entity.BdFile;
//import com.bdpick.domain.FileType;
//import com.bdpick.domain.Shop;
//import com.bdpick.domain.ShopImage;
//import com.bdpick.domain.request.CommonResponse;
//import com.bdpick.domain.request.ResponseCode;
//import com.bdpick.repository.FileRepository;
//import com.bdpick.repository.ShopImageRepository;
//import com.bdpick.repository.ShopRepository;
//import lombok.Data;
//import lombok.extern.slf4j.Slf4j;
//import org.apache.commons.lang3.StringUtils;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.http.MediaType;
//import org.springframework.http.codec.multipart.FilePart;
//import org.springframework.transaction.annotation.Transactional;
//import org.springframework.web.bind.annotation.*;
//import org.springframework.web.reactive.function.client.WebClient;
//import org.springframework.web.util.DefaultUriBuilderFactory;
//import reactor.core.publisher.Flux;
//import reactor.core.publisher.Mono;
//import reactor.core.scheduler.Schedulers;
//import reactor.util.context.Context;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.time.LocalDateTime;
//import java.util.*;
//
//import static com.bdpick.common.BdConstants.PREFIX_API_URL;
//
//@Data
//class Part {
//    Path filePath;
//    FilePart part;
//}
//
//@RestController
//@Slf4j
//@RequestMapping(value = PREFIX_API_URL + "/shops", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE, MediaType.APPLICATION_JSON_VALUE})
//public class ShopController {
//
//    @Value("${openapi.token}")
//    String openApiToken;
//
//    @Value("${upload-path}")
//    String uploadPath;
//
//    final private ShopRepository shopRepository;
//    final private ShopImageRepository shopImageRepository;
//    final private FileRepository fileRepository;
//    final JwtService jwtService;
//    private final String ERROR_NAME_DUPLICATE_REGISTER = "DuplicateNumber";
//
//    public ShopController(ShopRepository shopRepository, ShopImageRepository shopImageRepository, FileRepository fileRepository, JwtService jwtService) {
//        this.shopRepository = shopRepository;
//        this.shopImageRepository = shopImageRepository;
//        this.fileRepository = fileRepository;
//        this.jwtService = jwtService;
//    }
//
//    @GetMapping("this")
//    public Mono<CommonResponse> selectMyShop(@RequestHeader Map<String, Object> map) {
//        String token = BdUtil.getTokenByHeader(map);
//        String userId = jwtService.getUserIdByToken(token);
//        return shopRepository.findShopByUserId(userId)
//                .map(CommonResponse::new);
//    }
//
//
//    @PostMapping(value = "check-register")
//    public Mono<CommonResponse> checkRegistNumber(@RequestBody Shop shop) {
//        CommonResponse response = new CommonResponse();
//        String registerNumber = shop.getRegistNumber();
//
//        // request parameter 설정
//        Map<String, Object> bodyMap = new HashMap<>();
//        List<String> bnoList = new ArrayList<>();
//
//        bnoList.add(registerNumber);
//        bodyMap.put("b_no", bnoList);
//
//        // 공곰 API 호출 설정
//        DefaultUriBuilderFactory factory = new DefaultUriBuilderFactory("https://api.odcloud.kr/api/nts-businessman/v1/");
//        factory.setEncodingMode(DefaultUriBuilderFactory.EncodingMode.VALUES_ONLY);
//        WebClient client = WebClient
//                .builder()
//                .uriBuilderFactory(factory)
//                .build();
//
//        return shopRepository.findShopByRegistNumber(registerNumber)
//                .hasElement()
//                .flatMap(aBoolean -> {
//                    // 해당 사업자번호가 존재할 경우
//                    if (aBoolean) {
//                        response.setError("이미 존재하는 사업자번호 입니다.", false);
//                    } else {
//                        return client
//                                .post()
//                                .uri("status?serviceKey=" + openApiToken)
//                                .body(Mono.just(bodyMap), Map.class)
//                                .exchangeToMono(clientResponse -> clientResponse.bodyToMono(Map.class))
//                                .map(o -> {
//                                    Optional<Map<String, Object>> result = Optional.ofNullable(o);
//                                    Optional<List<Map<String, Object>>> dataList = result.map(stringObjectMap -> (List<Map<String, Object>>) stringObjectMap.get("data"));
//                                    Optional<Map<String, Object>> resultMap = dataList.stream().findAny().orElseGet(ArrayList::new).stream().findFirst();
//                                    Optional<String> bSttCd = resultMap.map(stringObjectMap -> (String) stringObjectMap.get("b_stt_cd"));
//                                    if (bSttCd.isEmpty() || bSttCd.get().equals("")) {
//                                        response.setError("사업자 번호가 유효하지 않습니다.", false);
//                                    } else {
//                                        switch (bSttCd.get()) {
//                                            // 계속사업자
//                                            case "01":
//                                                response.setData(true);
//                                                break;
//                                            // 휴업사업자
//                                            case "02":
//                                                response.setError("휴업 사업자 입니다.", false);
//                                                break;
//                                            // 폐업자
//                                            case "03":
//                                                response.setError("폐업 사업자 입니다.", false);
//                                                break;
//                                        }
//                                    }
//                                    return response;
//                                });
//                    }
//                    return Mono.just(response);
//                });
//    }
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
//    @Transactional
//    @PostMapping
//    public Mono<CommonResponse> createShop(@RequestHeader Map<String, Object> headerMap,
//                                           @RequestPart("files") Flux<FilePart> files,
//                                           @RequestPart("fileTypes") Flux<String> filesTypes,
//                                           @RequestPart("shop")Shop shop) {
//
//        String userId = jwtService.getUserIdByHeaderMap(headerMap);
//        // 매장 정보 저장
//        Mono<Long> shopIdMono = shopRepository.getSequence()
//                .flatMap(sequence -> {
//                    shop.setNew(true);
//                    shop.setId(sequence);
//                    shop.setUserId(userId);
//                    shop.setCreatedAt(LocalDateTime.now());
//                    shop.setUpdatedAt(LocalDateTime.now());
//                    shop.setAddressId(0L);
//                    return shopRepository.save(shop);
//                })
//                .mapNotNull(Shop::getId)
//                .cache();
//
//        // 오라클 업로드
//        return shopRepository.findShopByRegistNumber(StringUtils.rightPad(shop.getRegistNumber(), 10))
//                .hasElement()
//                .doOnNext(aBoolean -> {
//                    if (aBoolean) {
//                        throw new RuntimeException(ERROR_NAME_DUPLICATE_REGISTER);
//                    }
//                })
//                .thenMany(files.zipWith(filesTypes))
//                .flatMap(objects -> {
//                    FilePart filePart = objects.getT1();
//                    String fileType = objects.getT2();
////                    return ociService.uploadFile(filePart, fileType, "shop");
//                    return BdUtil.uploadFile(filePart, fileType, "images");
//                })
//                .zipWith(fileRepository.getSequence().repeat())
//                .map(objects -> {
//                    BdFile file = objects.getT1();
//                    long fileSeq = objects.getT2();
//                    file.setNew(true);
//                    file.setId(fileSeq);
//                    return file;
//                })
//                // 파일 insert
//                .flatMap(fileRepository::save)
//                .transformDeferredContextual((bdFileFlux, contextView) -> {
//                    Mono<Long> shopId = contextView.get("shopId");
//                    return bdFileFlux.zipWith(Mono.just(shopId).repeat());
//                })
//                .flatMap(objects -> Mono.just(objects.getT1()).zipWith(objects.getT2()))
//                .map(objects -> {
//                    BdFile file = objects.getT1();
//                    long shopId = objects.getT2();
//                    ShopImage shopImage = new ShopImage();
//                    shopImage.setNew(true);
//                    shopImage.setShopId(shopId);
//                    shopImage.setFileId(file.getId());
//                    shopImage.setType(FileType.valueOf(file.getFileType()));
//                    shopImage.setDisplayOrder(1);
//                    shopImage.setCreatedAt(LocalDateTime.now());
//                    return shopImage;
//                })
//                .zipWith(shopImageRepository.getSequence().repeat())
//                .flatMap(objects -> {
//                    ShopImage shopImage = objects.getT1();
//                    long shopImageId = objects.getT2();
//                    shopImage.setId(shopImageId);
//                    return shopImageRepository.save(shopImage);
//                })
//                .contextWrite(Context.of("shopId", shopIdMono))
//                .then()
//                .thenReturn(new CommonResponse(ResponseCode.CODE_SUCCESS, ResponseCode.MESSAGE_SUCCESS, true))
//                .onErrorResume(throwable -> {
//                    log.error("throwable = ", throwable);
//                    if (throwable instanceof RuntimeException) {
//                        if (throwable.getMessage().equals(ERROR_NAME_DUPLICATE_REGISTER)) {
//                            return Mono.just(new CommonResponse(ResponseCode.CODE_ERROR, "해당 사업자 번호는 이미 가입되어있습니다.", false));
//                        }
//                    } else return Mono.error(throwable);
//                    return Mono.error(throwable);
//                })
//                .onErrorReturn(new CommonResponse(ResponseCode.CODE_ERROR, ResponseCode.MESSAGE_ERROR, false));
//    }
//}