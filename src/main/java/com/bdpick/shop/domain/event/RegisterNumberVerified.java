package com.bdpick.shop.domain.event;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 사업자 번호 검증 이벤트 클래스
 */

@Getter
@Setter
@AllArgsConstructor
public class RegisterNumberVerified {
    private String registerNumber;
}
