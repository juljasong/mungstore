package com.mung.member.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
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

    @Value("${jwt.token.key}")
    private String key;

    @Value("${jwt.token.access-expiration-time}")
    private Long accessExpirationTime;

    @Value("${jwt.token.refresh-expiration-time}")
    private Long refreshExpirationTime;

    public String encodeBase64SecretKey(String secretKey) {
        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public SecretKey getKeyFromBase64EncodedKey(String base64EncodedSecretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Jws<Claims> parseJwtToken(String jwtToken) {
        return Jwts.parser()
                .verifyWith(getKeyFromBase64EncodedKey(key))
                .build()
                .parseSignedClaims(jwtToken);
    }

    public String createAccessToken(Long memberId) {
        return Jwts.builder()
                .subject("access-token")
                .claim("id", memberId)
                .claim("ref", new Date(System.currentTimeMillis() + refreshExpirationTime))
                .expiration(new Date(System.currentTimeMillis() + accessExpirationTime))
                .issuedAt(new Date())
                .signWith(getKeyFromBase64EncodedKey(key))
                .compact();
    }

    public String createRefreshToken(Long memberId) {
        String refreshToken = Jwts.builder()
                .subject("refresh-token")
                .claim("id", memberId)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + refreshExpirationTime))
                .signWith(getKeyFromBase64EncodedKey(key))
                .compact();

        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        valueOperations.set("rt-" + memberId, refreshToken, refreshExpirationTime, TimeUnit.MILLISECONDS);

        return refreshToken;
    }

    public boolean hasRefreshToken(Long memberId) {
        ValueOperations<String, String> valueOperations = redisTemplate.opsForValue();
        String refreshToken = valueOperations.get("rt-" + memberId);
        return StringUtils.hasText(refreshToken);
    }
}
