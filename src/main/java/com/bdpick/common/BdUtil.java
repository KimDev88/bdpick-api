package com.bdpick.common;

import com.bdpick.domain.dto.BdFileDto;
import com.bdpick.domain.entity.BdFile;
import com.bdpick.mapper.BdFileMapper;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.MediaType;
import org.springframework.http.client.MultipartBodyBuilder;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

@Slf4j
public class BdUtil {
    static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.setPropertyNamingStrategy(PropertyNamingStrategies.UPPER_SNAKE_CASE);
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }


    /**
     * header map 에서 jwt token 추출
     *
     * @param headerMap request header map
     * @return jwt token
     */
    public static String getTokenByHeader(Map<String, Object> headerMap) throws Exception {
        Optional<Map<String, Object>> optionalMap = Optional.ofNullable(headerMap);
        return optionalMap.stream()
                .map(stringObjectMap -> (String) stringObjectMap.get("authorization"))
                .flatMap(s -> Arrays.stream(s.split("Bearer ")))
                .filter(s -> !"".equals(s))
                .findAny()
                .orElse(null);
    }


    public static Mono<BdFile> uploadFile(FilePart part, String fileType, String directoryName) {
//    public static CompletionStage<BdFile> uploadFile(FilePart part, String fileType, String directoryName) {
        try {
//            String uri = "http://127.0.0.1:9090/api/file-upload";
            String uri = "http://152.69.231.150:9090/api/file-upload";
            String fileName = part.filename();
            String extension = FilenameUtils.getExtension(fileName);

            return getByteArray(part)
                    .flatMap(bytes -> {
                        MultipartBodyBuilder builder = new MultipartBodyBuilder();
                        builder.part("directory", directoryName + "/" + fileType);
                        String header = String.format("form-data; name=%s; filename=%s", "file", fileName);
                        builder.part("file", new ByteArrayResource(bytes))
                                .header("Content-Disposition", header);

                        return WebClient.builder()
                                .defaultHeader("Content-Type", MediaType.MULTIPART_FORM_DATA_VALUE)
                                .build()
                                .post()
                                .uri(uri)
                                .body(BodyInserters.fromMultipartData(builder.build()))
                                .retrieve()
                                .bodyToMono(String.class)
                                .map(string -> {
                                    BdFileDto file = new BdFileDto();
                                    file.setOriName(fileName);
                                    file.setDestName(fileName);
                                    file.setExtension(extension);
                                    file.setUri(string);
                                    file.setSize(bytes.length);
                                    file.setCreatedAt(LocalDateTime.now());
                                    file.setUpdatedAt(LocalDateTime.now());
                                    file.setFileType(fileType);
                                    BdFile bdFile = BdFileMapper.INSTANCE.BdFileDtoToBdfile(file);
                                    return bdFile;
                                })
                                .onErrorResume(throwable -> {
                                    log.error("error : ", throwable);
//                                    return Mono.error(throwable);
                                    throw new RuntimeException(throwable);
                                });
                    });
        } catch (Exception e) {
            throw new RuntimeException(e);
//            return Mono.error(e);
        }
//
    }

    public static Mono<byte[]> getByteArray(FilePart filePart) {
        return DataBufferUtils.join(filePart.content())
                .map(dataBuffer -> {
                    DataBufferUtils.release(dataBuffer);
                    try (DataBuffer.ByteBufferIterator byteBufferIterator = dataBuffer.writableByteBuffers()) {
                        return byteBufferIterator.next().array();
                    }
                });
    }

    public static <T> T objectConvert(Object object, Class<T> toValueType) {
        return objectMapper.convertValue(object, toValueType);
    }

    /**
     * get bean from applicationContext
     *
     * @param beanId       beanId
     * @param requiredType required class type
     * @param <T>          object type
     * @return bean object
     */
    public static <T> T getBean(String beanId, Class<T> requiredType) {

        ApplicationContext context = ApplicationContextProvider.getApplicationContext();
        return context.getBean(beanId, requiredType);
    }
}

