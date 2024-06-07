package com.mung.member.config;

import com.mung.common.exception.BadRequestException;
import com.mung.member.domain.AccessToken;
import com.mung.member.domain.RefreshToken;
import com.mung.member.repository.AccessTokenRedisRepository;
import com.mung.member.repository.RefreshTokenRedisRepository;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Optional;
import javax.crypto.SecretKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JwtUtil {

    public static final Long ACCESS_EXPIRATION_TIME = 1000 * 60 * 30L;
    public static final Long REFRESH_EXPIRATION_TIME = 1000 * 60 * 60 * 6L;
    private static String jwtKey;
    private final AccessTokenRedisRepository accessTokenRedisRepository;
    private final RefreshTokenRedisRepository refreshTokenRedisRepository;

    @Value("${jwt.key}")
    public void setSecretKey(String jwtKey) {
        JwtUtil.jwtKey = jwtKey;
    }

    private String encodeBase64SecretKey(String secretKey) {
        return Encoders.BASE64.encode(secretKey.getBytes(StandardCharsets.UTF_8));
    }

    private SecretKey getKeyFromBase64EncodedKey(String base64EncodedSecretKey) {
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
        Long memberId = null;
        try {
            memberId = Long.valueOf(Jwts.parser()
                .verifyWith(getKeyFromBase64EncodedKey(jwtKey))
                .build()
                .parseSignedClaims(jwtToken)
                .getPayload()
                .getId());
        } catch (JwtException e) {
            log.error(":: JwtUtil.getMemberId :: ", e);
            throw new BadRequestException();
        } catch (Exception e) {
            log.error(":: JwtUtil.getMemberId :: ", e);
        }
        return memberId;
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

    public Optional<AccessToken> checkAndGetAccessToken(String jwtToken) {
        return accessTokenRedisRepository.findById(getMemberId(jwtToken))
            .filter(t -> t.getAccessToken().equals(jwtToken));
    }

    public Optional<RefreshToken> checkAndGetRefreshToken(String jwtToken) {
        return refreshTokenRedisRepository.findById(getMemberId(jwtToken))
            .filter(t -> t.getRefreshToken().equals(jwtToken));
    }

    public void clearAccessAndRefreshToken(String jwt) {
        removeAccessToken(jwt);
        removeRefreshTokenByAccessToken(jwt);
    }

    private void removeAccessToken(String jwt) {
        AccessToken accessToken = checkAndGetAccessToken(jwt)
            .orElseThrow(BadRequestException::new);
        accessTokenRedisRepository.delete(accessToken);
    }

    private void removeRefreshTokenByAccessToken(String jwt) {
        RefreshToken refreshToken = refreshTokenRedisRepository.findById(getMemberId(jwt))
            .orElseThrow(BadRequestException::new);
        refreshTokenRedisRepository.delete(refreshToken);
    }

}
