package com.bdpick.common;

import com.bdpick.domain.BdFile;
import com.bdpick.repository.FileRepository;
import com.oracle.bmc.ClientConfiguration;
import com.oracle.bmc.ConfigFileReader;
import com.oracle.bmc.Region;
import com.oracle.bmc.auth.AuthenticationDetailsProvider;
import com.oracle.bmc.auth.ConfigFileAuthenticationDetailsProvider;
import com.oracle.bmc.objectstorage.ObjectStorage;
import com.oracle.bmc.objectstorage.ObjectStorageClient;
import com.oracle.bmc.objectstorage.model.*;
import com.oracle.bmc.objectstorage.requests.*;
import com.oracle.bmc.objectstorage.responses.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.FilenameUtils;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@Service
public class OciService {
    static ConfigFileReader.ConfigFile configFile;

    ClientConfiguration clientConfig;

    AuthenticationDetailsProvider provider;

    ObjectStorage client;

    String namespaceName;
    Bucket bucket;

    final String bucketName = "bd-pick";

    final FileRepository fileRepository;

    String bucketPrefixUrl;


    public OciService(FileRepository fileRepository) {
        this.fileRepository = fileRepository;
    }


    public void initialize() {
        try {
            if (configFile == null) {
                configFile = ConfigFileReader.parse("~/.oci/config", "DEFAULT");
            }

            if (clientConfig == null) {
                clientConfig
                        = ClientConfiguration.builder()
                        .connectionTimeoutMillis(3000)
                        .readTimeoutMillis(60000)
                        .build();

            }
            if (provider == null) {
                provider = new ConfigFileAuthenticationDetailsProvider(configFile);
            }

            if (client == null) {
                client =
                        ObjectStorageClient.builder().region(Region.AP_CHUNCHEON_1).build(provider);

            }

            if (namespaceName == null) {
                GetNamespaceResponse namespaceResponse =
                        client.getNamespace(GetNamespaceRequest.builder().build());
                namespaceName = namespaceResponse.getValue();
                System.out.println("Using namespace: " + namespaceName);
            }

            if (bucket == null) {
                // get bucket info
                GetBucketRequest getBucketRequest = GetBucketRequest.builder()
                        .namespaceName(namespaceName)
                        .bucketName(bucketName)
                        .build();
                GetBucketResponse response = client.getBucket(getBucketRequest);
                Bucket bucket = response.getBucket();
                log.debug(String.valueOf(bucket.getApproximateSize()));
                bucket.getApproximateSize();
            }

            if (bucketPrefixUrl == null) {
                bucketPrefixUrl = "https://objectstorage.ap-chuncheon-1.oraclecloud.com/n/"
                        + namespaceName
                        + "/b/"
                        + bucketName
                        + "/o/";
            }


        } catch (Exception e) {
            log.error("error : ", e);
        }

    }

    public Mono<BdFile> uploadFile(FilePart filePart, String fileType, String directoryName) {
//    public Mono<BdFile> uploadFile(Mono<FilePart> filePart2, Mono<String> fileType, String directoryName) {
        initialize();
        if (client == null) {
            throw new RuntimeException("client is null ");
        }

        AtomicInteger number = new AtomicInteger(1);
        if (directoryName == null) {
            directoryName = "";
        } else {
            directoryName += "/";
        }
//        String tmpDir = System.getProperty("java.io.tmpdir");
        String tmpDir = "./";
//        try {
        String finalDirectoryName = directoryName;

        String fileName = filePart.filename();
        String objectName = finalDirectoryName + fileType + "/" + fileName;
        Path filePath = Path.of(tmpDir + fileName);
        String extension = FilenameUtils.getExtension(fileName);
        /// create file
        filePart.transferTo(filePath).subscribe();

        // 파일이 바로 생성되지 않을 때가 있음
        while (true) {
            try {
                if (Files.isReadable(filePath) && Files.readAllBytes(filePath).length > 0) {
                    byte[] bytes = Files.readAllBytes(filePath);

                    if (Files.isReadable(filePath)) {
                        Files.delete(filePath);
                        log.debug(filePath + " deleted");
                    }

                    /// create
                    CreateMultipartUploadResponse createResponse = createMultipart(objectName, "image/" + extension);
                    MultipartUpload createdObject = createResponse.getMultipartUpload();
                    String uploadId = createdObject.getUploadId();

                    // upload
                    UploadPartResponse uploadResponse = uploadPart(objectName, uploadId, number.get(), new ByteArrayInputStream(bytes));
                    String etag = uploadResponse.getETag();

                    // commit
                    CommitMultipartUploadResponse commitResponse = commitMultipart(objectName, uploadId, number.getAndIncrement(), etag);
//                        client.close();


                    String path = bucketPrefixUrl + URLEncoder.encode(objectName, Charset.defaultCharset());

                    Map<String, Object> mapTest = new HashMap<>();
                    mapTest.put("fileName", fileName);
                    mapTest.put("destName", fileName);
                    mapTest.put("extension", extension);
                    mapTest.put("uri", path);
                    mapTest.put("size", bytes.length);
                    mapTest.put("createdAt", LocalDateTime.now());
                    mapTest.put("updatedAt", LocalDateTime.now());

                    // db insert
                    BdFile file = new BdFile();
                    file.setOriName(fileName);
                    file.setDestName(fileName);
                    file.setExtension(extension);
                    file.setUri(path);
                    file.setSize(bytes.length);
                    file.setCreatedAt(LocalDateTime.now());
                    file.setUpdatedAt(LocalDateTime.now());
                    file.setFileType(fileType);
                    return Mono.just(file);

                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
    }

    private CreateMultipartUploadResponse createMultipart(String objectName, String contentType) {
        CreateMultipartUploadDetails createDetails = CreateMultipartUploadDetails.builder()
                .object(objectName)
                .contentType(contentType)
                .build();
        CreateMultipartUploadRequest createRequest = CreateMultipartUploadRequest.builder()
                .namespaceName(namespaceName)
                .bucketName(bucketName)
                .createMultipartUploadDetails(createDetails)
                .build();
        return client.createMultipartUpload(createRequest);


    }

    private UploadPartResponse uploadPart(String objectName, String uploadId, Integer uploadPartNum, InputStream uploadPartBody) {
        UploadPartRequest uploadRequest = UploadPartRequest.builder()
                .namespaceName(namespaceName)
                .bucketName(bucketName)
                .objectName(objectName)
                .uploadId(uploadId)
                .uploadPartNum(uploadPartNum)
                .uploadPartBody(uploadPartBody)
                .build();

        /* Send request to the Client */
        return client.uploadPart(uploadRequest);
    }

    private CommitMultipartUploadResponse commitMultipart(String objectName, String uploadId, Integer uploadPartNum, String etag) {
        CommitMultipartUploadDetails commitDetails = CommitMultipartUploadDetails.builder()
                .partsToCommit(new ArrayList<>(Collections.singletonList(CommitMultipartUploadPartDetails.builder()
                        .partNum(uploadPartNum)
                        .etag(etag).build())))
                .build();

        CommitMultipartUploadRequest commitRequest = CommitMultipartUploadRequest.builder()
                .namespaceName(namespaceName)
                .bucketName(bucketName)
                .objectName(objectName)
                .uploadId(uploadId)
                .commitMultipartUploadDetails(commitDetails)
                .build();

        return client.commitMultipartUpload(commitRequest);

    }

}
