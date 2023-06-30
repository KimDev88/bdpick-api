//package com.bdpick.common.security;
//
//import org.springframework.http.HttpHeaders;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContext;
//import org.springframework.security.core.context.SecurityContextImpl;
//import org.springframework.security.web.server.context.ServerSecurityContextRepository;
//import org.springframework.stereotype.Service;
//import org.springframework.web.server.ServerWebExchange;
//import reactor.core.publisher.Mono;
//@Service
//public class SecurityContextRepository implements ServerSecurityContextRepository {
//
//    private final AuthenticationManager authenticationManager;
//
//    public SecurityContextRepository(AuthenticationManager authenticationManager) {
//        this.authenticationManager = authenticationManager;
//    }
//
//    @Override
//    public Mono<Void> save(ServerWebExchange swe, SecurityContext sc) {
//        return Mono.empty();
//    }
//
//    @Override
//    public Mono<SecurityContext> load(ServerWebExchange swe) {
//        return Mono.justOrEmpty(swe.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
//                .filter(authHeader -> authHeader.startsWith("Bearer "))
//                .flatMap(authHeader -> {
//                    String authToken = authHeader.substring(7);
//                    Authentication auth = new UsernamePasswordAuthenticationToken(authToken, authToken);
//                    return this.authenticationManager.authenticate(auth).map(SecurityContextImpl::new);
//                });
//    }
//}