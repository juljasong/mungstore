package com.mung.member.config;

import com.mung.common.exception.BadRequestException;
import com.mung.member.repository.AccessTokenRedisRepository;
import com.mung.member.repository.RefreshTokenRedisRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    @Mock
    private AccessTokenRedisRepository accessTokenRedisRepository;
    @Mock
    private RefreshTokenRedisRepository refreshTokenRedisRepository;
    @InjectMocks
    private JwtUtil jwtUtil;

    private String jwtToken;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(jwtUtil, "jwtKey",
            "/VLfdrwEn0MZScSqq6En7NMOW9rF7SQHN7LiWGCG21g=");
        this.jwtToken = jwtUtil.createToken(15L, JwtUtil.ACCESS_EXPIRATION_TIME);
    }

    @Test
    public void jwt로memberId추출_성공() throws Exception {
        // when
        Long memberId = jwtUtil.getMemberId(jwtToken);

        // then
        assertEquals(15L, memberId);
    }

    @Test
    public void jwt로memberId추출_실패() {
        // when
        String jwt = "abc";

        // expected
        assertThrows(BadRequestException.class,
            () -> jwtUtil.getMemberId(jwt));
    }

    @Test
    public void accessJwt생성_성공() throws Exception {
        // given
        Long memberId = 15L;
        Long expiration = JwtUtil.ACCESS_EXPIRATION_TIME;

        // when
        String token = jwtUtil.createToken(memberId, expiration);

        // then
        assertNotNull(token);
        verify(accessTokenRedisRepository, times(2)).save(any());
    }

    @Test
    public void refreshJwt생성_성공() throws Exception {
        // given
        Long memberId = 15L;
        Long expiration = JwtUtil.REFRESH_EXPIRATION_TIME;

        // when
        String token = jwtUtil.createToken(memberId, expiration);

        // then
        assertNotNull(token);
        verify(refreshTokenRedisRepository).save(any());
    }

}