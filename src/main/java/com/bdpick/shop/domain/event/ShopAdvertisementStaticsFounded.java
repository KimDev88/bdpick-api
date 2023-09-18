package com.bdpick.shop.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 광고 통계 정보 조회됨
 */
@Getter
@Setter
@AllArgsConstructor
public class ShopAdvertisementStaticsFounded {
    private long clickCount;
    private long favoriteCount;
    private long UploadCount;
}
