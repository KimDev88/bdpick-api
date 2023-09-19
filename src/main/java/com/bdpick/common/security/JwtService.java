package com.bdpick.common.security;

import com.bdpick.common.BdUtil;
import com.bdpick.user.domain.enumeration.TokenType;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.Map;
import java.util.Objects;

@Service
@Slf4j
public class JwtService {
    private static Key secretKey;

    @Value("${jwtSecretKey}")
    public void setSecretKey(String stringKey) {
        byte[] keyBytes = Decoders.BASE64.decode(stringKey);
        JwtService.secretKey = Keys.hmacShaKeyFor(keyBytes);
    }

//    public Boolean validateToken(String token) {
//        try {
//            Jwts.parserBuilder().setSigningKey(secretKey).build().parseClaimsJws(token).getBody();
//            return true;
//        } catch (Exception e) {
//            throw new RuntimeException("인증 실패");
//        }
//    }

    public static String createAccessToken(String userId) {
        return createToken(TokenType.A, userId);
    }

    public static String createRefreshToken() {
        return createToken(TokenType.R, null);
    }

    private static String createToken(TokenType type, String id) {
        Claims claims = Jwts.claims().setSubject(type.name()); // sub
        claims.setIssuer("BDPICK");
        LocalDateTime time = LocalDateTime.now();
        switch (type) {
            // access token
            case A -> {
//                time = time.plusMinutes(5);
                time = time.plusYears(1);
                claims.put("id", id);
            }
            // refresh token
//            case R -> time = time.plusSeconds(5);
            case R -> time = time.plusDays(5);
        }
        Date expiredDate = Date.from(time.atZone(ZoneId.systemDefault()).toInstant());
        // 만료 시간
        claims.setExpiration(expiredDate); // exp
        // 시작 시간
//        claims.setNotBefore(expired);
        // 발행 시간
        claims.setIssuedAt(new Date());

        String jws = Jwts.builder()
                .setClaims(claims)
                .signWith(secretKey, SignatureAlgorithm.HS256)
                .compact();
        return jws;
    }

    /**
     * 토큰 검증
     *
     * @param token jwt token
     * @return jws
     */
    public Jws<Claims> verifyToken(String token) {
        try {
            JwtParser parser = Jwts.parserBuilder().setSigningKey(secretKey).build();
            Jws<Claims> claimsJws = parser.parseClaimsJws(token);

            TokenType tokenType = TokenType.valueOf(claimsJws.getBody().getSubject());
            return claimsJws;
        } catch (ExpiredJwtException e) {
            log.error("expired", e);
            throw e;
        } catch (PrematureJwtException e) {
            log.error("not yet", e);
            throw e;
        } catch (Exception e) {
            log.error("exception", e);
            throw e;
        }
    }

    public String getUserIdByToken(String token) {
        return Objects.requireNonNull(verifyToken(token))
                .getBody()
                .get("id", String.class);

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
    }

    public String getUserIdByHeaderMap(Map<String, Object> headerMap) throws Exception {
        return Objects.requireNonNull(verifyToken(BdUtil.getTokenByHeader(headerMap)))
                .getBody()
                .get("id", String.class);
    }

//    public Long getShopIdByHeaderMap(Map<String, Object> headerMap) {
//        return getShopIdByUserId(getUserIdByHeaderMap(headerMap));
//    }


}