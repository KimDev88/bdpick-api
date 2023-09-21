package com.bdpick.user.domain.event;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

/**
 * 아이디 검색됨
 */
@Getter
@Setter
@AllArgsConstructor
public class IdFound {
    private String email;
}
