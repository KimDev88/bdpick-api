package com.bdpick.shop.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 가게 등록 이벤트 클래스
 */
@Getter
@Setter
@AllArgsConstructor
public class ShopCreated {
    private Long id;
}
