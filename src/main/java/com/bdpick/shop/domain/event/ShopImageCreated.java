package com.bdpick.shop.domain.event;

import com.bdpick.shop.domain.ShopImage;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

/**
 * 가게 이미지 생성 이벤트 클래스
 */
@Getter
@Setter
@AllArgsConstructor
public class ShopImageCreated {
    private long shopId;
    private List<ShopImage> imageList;
}
