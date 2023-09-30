package com.bdpick.advertisement.web.rest.dto;

import lombok.Data;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class ShopAdDto implements Serializable {
    private Long id;
    private Long shopId;
    private String branchName;
    private LocalDateTime startedAt;
    private LocalDateTime endedAt;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private String fileUri;
    private String keywords;
    private List<String> keywordList;
}
