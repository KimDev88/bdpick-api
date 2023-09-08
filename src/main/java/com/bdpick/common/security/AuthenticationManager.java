package com.bdpick.common.security;

import com.bdpick.repository.UserRepository;
import com.bdpick.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class AuthenticationManager implements ReactiveAuthenticationManager {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final UserService userService;

//    public AuthenticationManager(JwtService jwtService, UserRepository userRepository) {
//        this.jwtService = jwtService;
//        this.userRepository = userRepository;
//    }

    @Override
    public Mono<Authentication> authenticate(Authentication authentication) {
        String authToken = authentication.getCredentials().toString();
        String id = jwtService.getUserIdByToken(authToken);
        try {
            jwtService.verifyToken(authToken);
            return userService.findById(id)
                    .map(user -> (Authentication) new UsernamePasswordAuthenticationToken(
//                            new CustomUserPrincipal(jwtService, userRepository, user),
                            new CustomUserPrincipal(user),
                            user.getPassword(),
                            user.getAuthorities()))
                    .onErrorComplete(throwable -> {
                        System.out.println("throwable = " + throwable);
                        return false;
                    })
                    .onErrorResume(Mono::error);
        } catch (Exception e) {
            return Mono.error(e);
        }

    }
}