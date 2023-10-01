package com.bdpick.advertisement.web.rest.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;


/**
 * shop advertisement dto class
 */
@Getter
@Setter
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
//    private List<AdKeyword> keywordList;
//    private List<AdImage> adImageList = new ArrayList<>();

}
