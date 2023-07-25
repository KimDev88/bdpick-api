//package com.bdpick.common.security;
//
//import com.bdpick.common.BdUtil;
//import com.bdpick.domain.Shop;
//import com.bdpick.enums.TokenType;
//import com.bdpick.repository.ShopRepository;
//import com.fasterxml.jackson.databind.ObjectMapper;
//import io.jsonwebtoken.*;
//import io.jsonwebtoken.io.Decoders;
//import io.jsonwebtoken.security.Keys;
//import lombok.extern.log4j.Log4j2;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.stereotype.Service;
//
//import java.security.Key;
//import java.time.LocalDateTime;
//import java.time.ZoneId;
//import java.util.Date;
//import java.util.Map;
//import java.util.Objects;
//
//@Service
//@Log4j2
//public class JwtService {
//    private final Key secretKey;
//    private final ShopRepository shopRepository;
//
//    public JwtService(@Value("${jwtSecretKey}") String secretKey, ShopRepository shopRepository) {
//        this.shopRepository = shopRepository;
//        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
//        this.secretKey = Keys.hmacShaKeyFor(keyBytes);
//    }
//
////    public Boolean validateToken(String token) {
////        try {
////            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
////            return true;
////        } catch (Exception e) {
////            throw new RuntimeException("인증 실패");
////        }
////    }
//
//    public String createAccessToken(String userId) {
//        return createToken(TokenType.A, userId);
//    }
//
//    public String createRefreshToken() {
//        return createToken(TokenType.R, null);
//    }
//
//    private String createToken(TokenType type, String id) {
//        Claims claims = Jwts.claims().setSubject(type.name()); // sub
//        claims.setIssuer("BDPICK");
//        LocalDateTime time = LocalDateTime.now();
//        switch (type) {
//            case A:
////                time = time.plusMinutes(5);
//                time = time.plusYears(1);
//                claims.put("id", id);
//                break;
//            case R:
//                time = time.plusDays(5);
//                break;
//        }
//        Date expiredDate = Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
//        // 만료 시간
//        claims.setExpiration(expiredDate); // exp
//        // 시작 시간
////        claims.setNotBefore(expired);
//        // 발행 시간
//        claims.setIssuedAt(new Date());
//
//        String jws = Jwts.builder()
//                .setClaims(claims)
//                .signWith(secretKey, SignatureAlgorithm.HS256)
//                .compact();
//        return jws;
//    }
//
//    public Jws<Claims> verifyToken(String token) {
//        try {
//            JwtParser parser = Jwts.parserBuilder().setSigningKey(secretKey).build();
//            Jws<Claims> claimsJws = parser.parseClaimsJws(token);
//
//            TokenType tokenType = TokenType.valueOf(claimsJws.getBody().getSubject());
//            return claimsJws;
//        } catch (ExpiredJwtException e) {
//            log.error("expired", e);
//            throw e;
//        } catch (PrematureJwtException e) {
//            log.error("not yet", e);
//            throw e;
//        } catch (Exception e) {
//            log.error("exception", e);
//            throw e;
//        }
//    }
//
//    public String getUserIdByToken(String token) {
////        return Objects.requireNonNull(verifyToken(token))
////                .getBody()
////                .get("id", String.class);
//
//        String[] tokens = token.split("\\.");
//        ObjectMapper objectMapper = new ObjectMapper();
//        try {
//            Map<String, Object> map = objectMapper.readValue(Decoders.BASE64URL.decode(tokens[1]), Map.class);
//            if (map.containsKey("id")) {
//                return (String) map.get("id");
//            }
//        } catch (Exception e) {
//            log.error("error : ", e);
//            return null;
//        }
//        return null;
//    }
//
//    public String getUserIdByHeaderMap(Map<String, Object> headerMap) {
//        return Objects.requireNonNull(verifyToken(BdUtil.getTokenByHeader(headerMap)))
//                .getBody()
//                .get("id", String.class);
//    }
//
//    public Long getShopIdByUserId(String userId) {
//        return shopRepository.findShopByUserId(userId)
//                .mapNotNull(Shop::getId).block();
//    }
//
//    public Long getShopIdByHeaderMap(Map<String, Object> headerMap) {
//        return getShopIdByUserId(getUserIdByHeaderMap(headerMap));
//    }
//
//}