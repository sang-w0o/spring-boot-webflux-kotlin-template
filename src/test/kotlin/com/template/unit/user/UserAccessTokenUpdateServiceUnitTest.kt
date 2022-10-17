package com.template.unit.user

import com.ninjasquad.springmockk.MockkBean
import com.template.security.exception.AuthenticateException
import com.template.security.tools.JwtTokenUtil
import com.template.unit.BaseUnitTest
import com.template.user.service.UserService
import com.template.util.JWT_REFRESH_TOKEN_EXP
import com.template.util.TOKEN
import com.template.util.USER_ID
import com.template.util.generateExpiredToken
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import org.junit.jupiter.api.*
import org.springframework.http.HttpStatus
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

class UserAccessTokenUpdateServiceUnitTest : BaseUnitTest() {

    private lateinit var userService: UserService

    @MockkBean
    private lateinit var jwtTokenUtil: JwtTokenUtil

    @BeforeEach
    fun setUp() {
        every { jwtTokenUtil.generateAccessToken(any()) } returns TOKEN
        every { jwtTokenUtil.generateRefreshToken(any()) } returns TOKEN
        every { jwtTokenUtil.extractUserId(any()) } returns USER_ID
        every { jwtTokenUtil.isTokenExpired(any()) } returns false
        jwtTokenUtil = JwtTokenUtil(jwtProperties, userRepository)
        userService = UserService(userRepository, jwtTokenUtil)
    }

    @DisplayName("Success")
    @Test
    fun success() {
        val user = getMockUser()
        every { userRepository.findById(any<String>()) } returns Mono.just(user)
        val refreshToken = jwtTokenUtil.generateRefreshToken(user.id!!)
        userService.updateAccessToken(Mono.just(refreshToken))
            .`as`(StepVerifier::create)
            .expectNextMatches {
                it.statusCode shouldBe HttpStatus.OK
                it.body!!.accessToken shouldNotBe null
                shouldNotThrowAny { jwtTokenUtil.verify(it.body!!.accessToken) }
                true
            }.verifyComplete()
    }

    @DisplayName("Fail - Expired refreshToken")
    @Test
    fun failWithExpiredRefreshToken() {
        val refreshToken = generateExpiredToken(JWT_REFRESH_TOKEN_EXP, jwtProperties.secret)
        userService.updateAccessToken(Mono.just(refreshToken))
            .`as`(StepVerifier::create)
            .expectErrorMatches {
                it.message shouldBe "Jwt 토큰이 만료되었습니다."
                it.shouldBeInstanceOf<AuthenticateException>()
                true
            }.verify()
    }

    @DisplayName("Fail - Invalid userId")
    @Test
    fun failWithInvalidUserId() {
        every { userRepository.findById(any<String>()) } returns Mono.empty()
        val refreshToken = jwtTokenUtil.generateRefreshToken(USER_ID)
        userService.updateAccessToken(Mono.just(refreshToken))
            .`as`(StepVerifier::create)
            .expectErrorMatches {
                it.message shouldBe "Invalid userId."
                it.shouldBeInstanceOf<AuthenticateException>()
                true
            }.verify()
    }
}
