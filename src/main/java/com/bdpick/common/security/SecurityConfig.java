//package com.bdpick.common.security;
//
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.http.HttpStatus;
//import org.springframework.security.authentication.ReactiveAuthenticationManager;
//import org.springframework.security.config.web.server.ServerHttpSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.web.server.SecurityWebFilterChain;
//import org.springframework.security.web.server.context.ServerSecurityContextRepository;
//import reactor.core.publisher.Mono;
//
//@Configuration
//public class SecurityConfig {
//    private ReactiveAuthenticationManager authenticationManager;
//    private ServerSecurityContextRepository securityContextRepository;
//
//    public SecurityConfig(ReactiveAuthenticationManager authenticationManager, ServerSecurityContextRepository securityContextRepository) {
//        this.authenticationManager = authenticationManager;
//        this.securityContextRepository = securityContextRepository;
//    }
//
//
//    @Bean
//    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity http) {
////        http.authorizeExchange((exchanges) -> exchanges
//////                        .pathMatchers("/api/sign/*")
////                        .pathMatchers("*")
////                        .permitAll()
//////                        .anyExchange().authenticated()
////                )
////                .formLogin().disable()
////                .csrf().disable()
////                .cors()
////                .and();
////                .exceptionHandling()
////                .authenticationEntryPoint((swe, e) -> Mono
////                        .fromRunnable(() -> swe.getResponse().setStatusCode(
////                                HttpStatus.UNAUTHORIZED)))
////                .accessDeniedHandler((swe, e) -> Mono.fromRunnable(() -> swe
////                        .getResponse().setStatusCode(HttpStatus.FORBIDDEN)))
////                .and()
////                .authenticationManager(authenticationManager)
////                .securityContextRepository(securityContextRepository);
//        return http.build();
//    }
//
//    @Bean
//    public PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//}
