package com.template.unit.auth

import com.template.security.exception.AuthenticateException
import com.template.security.tools.JwtTokenUtil
import com.template.unit.BaseUnitTest
import com.template.user.domain.User
import com.template.util.USER_ID
import com.template.util.generateExpiredToken
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.matchers.shouldBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

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
        jwtTokenUtil.isTokenExpired(refreshToken) shouldBe false
    }

    @DisplayName("만료된 AccessToken 검증")
    @Test
    fun oldAccessTokenIsExpired() {
        val oldAccessToken = generateExpiredToken(jwtProperties.accessTokenExp, jwtProperties.secret)
        val exception = shouldThrow<AuthenticateException> { jwtTokenUtil.verify(oldAccessToken) }
        exception.message shouldBe "Jwt 토큰이 만료되었습니다."
    }

    @DisplayName("만료된 RefreshToken 검증")
    @Test
    fun oldRefreshTokenIsExpired() {
        val oldRefreshToken = generateExpiredToken(jwtProperties.refreshTokenExp, jwtProperties.secret)
        val exception = shouldThrow<AuthenticateException> { jwtTokenUtil.verify(oldRefreshToken) }
        exception.message shouldBe "Jwt 토큰이 만료되었습니다."
    }

    @DisplayName("잘못된 형식의 Jwt 토큰")
    @Test
    fun wrongToken() {
        val wrongToken = "WRONG TOKEN"
        val exception = shouldThrow<AuthenticateException> { jwtTokenUtil.verify(wrongToken) }
        exception.message shouldBe "잘못된 형식의 Jwt 토큰입니다."
    }

    @DisplayName("Signature가 잘못된 Jwt가 주어진 경우")
    @Test
    fun wrongSignatureToken() {
        val wrongToken = generateOtherSignatureToken(jwtProperties.accessTokenExp)
        val exception = shouldThrow<AuthenticateException> { jwtTokenUtil.verify(wrongToken) }
        exception.message shouldBe "Jwt Signature이 잘못된 값입니다."
    }

    @DisplayName("Jwt Payload에 userId가 없는 경우")
    @Test
    fun jwtWithoutUserIdInPayload() {
        val wrongToken = generateTokenWithoutUserIdClaim()
        val exception = shouldThrow<AuthenticateException> { jwtTokenUtil.extractUserId(wrongToken) }
        exception.message shouldBe "JWT Claim에 userId가 없습니다."
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
                it.principal.shouldBeInstanceOf<User>()
                it.principal shouldBe user
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
                it.message shouldBe "Invalid userId"
                it.shouldBeInstanceOf<AuthenticateException>()
                true
            }
            .verify()
    }
}
