package com.mung.member.config;

import com.mung.member.domain.AccessToken;
import com.mung.member.domain.RefreshToken;
import com.mung.member.repository.AccessTokenRedisRepository;
import com.mung.member.repository.RefreshTokenRedisRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;

@Configuration
@RequiredArgsConstructor
public class JwtUtil {

    private final AccessTokenRedisRepository accessTokenRedisRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    private static String jwtKey;
    public final static Long ACCESS_EXPIRATION_TIME = 1000 * 60 * 30L;
    public final static Long REFRESH_EXPIRATION_TIME = 1000 * 60 * 60 * 6L;

    @Value("${jwt.key}")
    public void setSecretKey(String jwtKey) {
        JwtUtil.jwtKey = jwtKey;
    }

    public String encodeBase64SecretKey(String secretKey) {
        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    public SecretKey getKeyFromBase64EncodedKey(String base64EncodedSecretKey) {
        byte[] keyBytes = Decoders.BASE64.decode(base64EncodedSecretKey);

        return Keys.hmacShaKeyFor(keyBytes);
    }

    public Jws<Claims> parseJwtToken(String jwtToken) {
        return Jwts.parser()
                .verifyWith(getKeyFromBase64EncodedKey(jwtKey))
                .build()
                .parseSignedClaims(jwtToken);
    }

    public Long getMemberId(String jwtToken) {
        return Long.valueOf(Jwts.parser()
                .verifyWith(getKeyFromBase64EncodedKey(jwtKey))
                .build()
                .parseSignedClaims(jwtToken)
                        .getPayload()
                        .getId());
    }

    public String createToken(Long memberId, Long expiration) {
        String jwt = Jwts.builder()
                .subject("token")
                .id(String.valueOf(memberId))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .issuedAt(new Date())
                .signWith(getKeyFromBase64EncodedKey(jwtKey))
                .compact();

        if (expiration == ACCESS_EXPIRATION_TIME) {
            accessTokenRedisRepository.save(AccessToken.builder()
                    .memberId(memberId)
                    .accessToken(jwt)
                    .build());
        } else if (expiration == REFRESH_EXPIRATION_TIME) {
            refreshTokenRedisRepository.save(RefreshToken.builder()
                    .memberId(memberId)
                    .refreshToken(jwt)
                    .build());
        }

        return jwt;
    }

    public boolean hasRefreshToken(Long id) throws BadRequestException {
        RefreshToken token = refreshTokenRedisRepository.findById(id).orElseGet(() -> null);
        return token != null;
    }

    public void removeRefreshToken(String accessToken) throws BadRequestException {
        RefreshToken refreshToken = refreshTokenRedisRepository.findById(getMemberId(accessToken))
                .orElseThrow(BadRequestException::new);
        refreshTokenRedisRepository.delete(refreshToken);
    }

}
