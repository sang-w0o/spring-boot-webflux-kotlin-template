package com.template.unit.auth

import com.template.security.exception.AuthenticateException
import com.template.security.tools.JwtTokenUtil
import com.template.unit.BaseUnitTest
import com.template.user.domain.User
import com.template.util.TestUtils
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import kotlin.test.assertFailsWith
import kotlin.test.assertTrue

class JwtTokenUtilTest : BaseUnitTest() {

    private lateinit var jwtTokenUtil: JwtTokenUtil

    @BeforeEach
    fun setUp() {
        jwtTokenUtil = JwtTokenUtil(jwtProperties, userRepository)
    }

    @DisplayName("RefreshToken 생성")
    @Test
    fun refreshTokenIsCreated() {
        val refreshToken = jwtTokenUtil.generateRefreshToken(USER_ID)
        assertFalse(jwtTokenUtil.isTokenExpired(refreshToken))
    }

    @DisplayName("만료된 AccessToken 검증")
    @Test
    fun oldAccessTokenIsExpired() {
        val oldAccessToken = TestUtils.generateExpiredToken(jwtProperties.accessTokenExp, jwtProperties.secret)
        val exception = assertFailsWith<AuthenticateException> { jwtTokenUtil.verify(oldAccessToken) }
        assertEquals("Jwt 토큰이 만료되었습니다.", exception.message)
    }

    @DisplayName("만료된 RefreshToken 검증")
    @Test
    fun oldRefreshTokenIsExpired() {
        val oldRefreshToken = TestUtils.generateExpiredToken(jwtProperties.refreshTokenExp, jwtProperties.secret)
        val exception = assertFailsWith<AuthenticateException> { jwtTokenUtil.verify(oldRefreshToken) }
        assertEquals("Jwt 토큰이 만료되었습니다.", exception.message)
    }

    @DisplayName("잘못된 형식의 Jwt 토큰")
    @Test
    fun wrongToken() {
        val wrongToken = "WRONG TOKEN"
        val exception = assertFailsWith<AuthenticateException> { jwtTokenUtil.verify(wrongToken) }
        assertEquals("잘못된 형식의 Jwt 토큰입니다.", exception.message)
    }

    @DisplayName("Signature가 잘못된 Jwt가 주어진 경우")
    @Test
    fun wrongSignatureToken() {
        val wrongToken = generateOtherSignatureToken(jwtProperties.accessTokenExp)
        val exception = assertFailsWith<AuthenticateException> { jwtTokenUtil.verify(wrongToken) }
        assertEquals("Jwt Signature이 잘못된 값입니다.", exception.message)
    }

    @DisplayName("Jwt Payload에 userId가 없는 경우")
    @Test
    fun jwtWithoutUserIdInPayload() {
        val wrongToken = generateTokenWithoutUserIdClaim()
        val exception = assertFailsWith<AuthenticateException> { jwtTokenUtil.extractUserId(wrongToken) }
        assertEquals("JWT Claim에 userId가 없습니다.", exception.message)
    }

    @DisplayName("정상적인 token일 경우 검증 성공")
    @Test
    fun correctTokenVerifyComplete() {
        val user = getMockUser()
        `when`(userRepository.findById(anyString())).thenReturn(Mono.just(user))
        val accessToken = jwtTokenUtil.generateAccessToken(USER_ID)
        jwtTokenUtil.verify(accessToken)
            .`as`(StepVerifier::create)
            .expectNextMatches {
                assertTrue(it.principal is User)
                assertEquals(it.principal, user)
                true
            }.verifyComplete()
    }

    @DisplayName("존재하지 않는 userId인 경우 검증 실패")
    @Test
    fun tokenWithInvalidUserId() {
        `when`(userRepository.findById(anyString())).thenReturn(Mono.empty())
        val accessToken = jwtTokenUtil.generateAccessToken(USER_ID)
        jwtTokenUtil.verify(accessToken)
            .`as`(StepVerifier::create)
            .expectErrorMatches {
                assertEquals("Invalid userId", it.message!!)
                assertTrue(it is AuthenticateException)
                true
            }
            .verify()
    }
}
