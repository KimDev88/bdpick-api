package com.bdpick.user.domain.event;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 아이디 검증 됨
 */
@Getter
@Setter
@AllArgsConstructor
public class IdVerified {
    private String id;
}
