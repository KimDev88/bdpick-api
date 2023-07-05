package com.bdpick.common;

import com.bdpick.domain.BdFile;
import lombok.extern.log4j.Log4j2;
import org.apache.commons.io.FilenameUtils;
import org.springframework.core.io.ByteArrayResource;
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

@Log4j2
public class BdUtil {
    public static String getTokenByHeader(Map<String, Object> headerMap) {
        Optional<Map<String, Object>> optionalMap = Optional.ofNullable(headerMap);
        return optionalMap.stream()
                .map(stringObjectMap -> (String) stringObjectMap.get("authorization"))
                .flatMap(s -> Arrays.stream(s.split("Bearer ")))
                .filter(s -> !"".equals(s))
                .findAny()
                .orElse(null);
    }


    public static Mono<BdFile> uploadFile(FilePart part, String fileType, String directoryName) {
//        String uri = "http://127.0.0.1:9090/api/file-upload";
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
                                BdFile file = new BdFile();
                                file.setOriName(fileName);
                                file.setDestName(fileName);
                                file.setExtension(extension);
                                file.setUri(string);
                                file.setSize(bytes.length);
                                file.setCreatedAt(LocalDateTime.now());
                                file.setUpdatedAt(LocalDateTime.now());
                                file.setFileType(fileType);
                                return file;
                            })
                            .onErrorResume(throwable -> {
                                log.error("error : ", throwable);
                                throw new RuntimeException(throwable);
                            });
                });
    }

    public static Mono<byte[]> getByteArray(FilePart filePart) {
        return DataBufferUtils.join(filePart.content())
                .map(dataBuffer -> {
                    DataBufferUtils.release(dataBuffer);
                    return dataBuffer.asByteBuffer().array();
                });
    }
}
