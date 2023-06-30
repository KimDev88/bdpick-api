//package com.bdpick.common.security;
//
//import com.bdpick.repository.UserRepository;
//import org.springframework.security.authentication.ReactiveAuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.stereotype.Service;
//import reactor.core.publisher.Mono;
//
//@Service
//public class AuthenticationManager implements ReactiveAuthenticationManager {
//
//    private final JwtService jwtService;
//    private final UserRepository userRepository;
//
//    public AuthenticationManager(JwtService jwtService, UserRepository userRepository) {
//        this.jwtService = jwtService;
//        this.userRepository = userRepository;
//    }
//
//    @Override
//    public Mono<Authentication> authenticate(Authentication authentication) {
//        String authToken = authentication.getCredentials().toString();
//        String id = jwtService.getUserIdByToken(authToken);
//        return Mono.just(jwtService.validateToken(authToken))
//                .filter(valid -> valid)
//                .switchIfEmpty(Mono.empty())
//                .flatMap(valid -> userRepository.findById(id))
//                .map(user -> new UsernamePasswordAuthenticationToken(
////                            new CustomUserPrincipal(jwtService, userRepository, user),
//                        new CustomUserPrincipal(user),
//                        user.getPassword(),
//                        user.getAuthorities()));
//    }
//}