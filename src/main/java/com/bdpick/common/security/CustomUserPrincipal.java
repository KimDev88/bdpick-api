package com.bdpick.common.security;

import com.bdpick.domain.User;

import java.io.Serializable;

public class CustomUserPrincipal extends User implements Serializable {

    private final User user;

    public CustomUserPrincipal(User user){
//                super(user.getId().toString(), user.getId().toString(), user.getAuthorities());
        this.user = user;
    }
}
