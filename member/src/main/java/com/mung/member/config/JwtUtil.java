package com.mung.member.config;

import com.mung.common.domain.RedisPrefix;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class JwtUtil {

    private final RedisTemplate<String, String> redisTemplate;

    private final static String KEY = "/VLfdrwEn0MZScSqq6En7NMOW9rF7SQHN7LiWGCG21g=";
    private final static Long ACCESS_EXPIRATION_TIME = 1800000L; // 30분
    private final static Long REFRESH_EXPIRATION_TIME = 21600000L; // 6시간

    public String encodeBase64SecretKey(String secretKey) {
        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public SecretKey getKeyFromBase64EncodedKey(String base64EncodedSecretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Jws<Claims> parseJwtToken(String jwtToken) {
        return Jwts.parser()
                .verifyWith(getKeyFromBase64EncodedKey(KEY))
                .build()
                .parseSignedClaims(jwtToken);
    }

    public Long getMemberId(String jwtToken) {
        System.out.println("jwtToken = " + jwtToken);
        return Long.valueOf(Jwts.parser()
                .verifyWith(getKeyFromBase64EncodedKey(KEY))
                .build()
                .parseSignedClaims(jwtToken)
                        .getPayload()
                        .getId());
    }

    public String createAccessToken(Long memberId) {
        return Jwts.builder()
                .subject("access-token")
                .id(String.valueOf(memberId))
                .expiration(new Date(System.currentTimeMillis() + ACCESS_EXPIRATION_TIME))
                .issuedAt(new Date())
                .signWith(getKeyFromBase64EncodedKey(KEY))
                .compact();
    }

    public String createRefreshToken(Long memberId) {
        String refreshToken = Jwts.builder()
                .subject("refresh-token")
                .id(String.valueOf(memberId))
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + REFRESH_EXPIRATION_TIME))
                .signWith(getKeyFromBase64EncodedKey(KEY))
                .compact();

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set(getRedisRefreshKey(memberId), refreshToken, REFRESH_EXPIRATION_TIME, TimeUnit.MILLISECONDS);

        return refreshToken;
    }

    public boolean hasRefreshToken(Long memberId) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String refreshToken = valueOperations.get(getRedisRefreshKey(memberId));
        return StringUtils.hasText(refreshToken);
    }

    public void removeRefreshToken(String jwtToken) {
        redisTemplate.delete(getRedisRefreshKey(getMemberId(jwtToken)));
    }

    private String getRedisRefreshKey(Long memberId) {
        return RedisPrefix.REFRESH_TOKEN_ + String.valueOf(memberId);
    }

}
