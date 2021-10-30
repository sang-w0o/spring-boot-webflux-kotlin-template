package com.template.unit.user

import com.template.security.exception.AuthenticateException
import com.template.security.tools.JwtTokenUtil
import com.template.unit.BaseUnitTest
import com.template.user.domain.User
import com.template.user.dto.AccessTokenUpdateRequestDto
import com.template.user.service.UserService
import com.template.util.JWT_REFRESH_TOKEN_EXP
import com.template.util.generateExpiredToken
import io.kotest.assertions.throwables.shouldNotThrowAny
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import org.junit.jupiter.api.*
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import reactor.core.publisher.Mono
import reactor.test.StepVerifier

@Tag("UserService-updateAccessToken")
class UserAccessTokenUpdateServiceUnitTest : BaseUnitTest() {

    private lateinit var userService: UserService

    private lateinit var jwtTokenUtil: JwtTokenUtil

    private lateinit var user: User

    @BeforeEach
    fun setUp() {
        user = getMockUser()
        `when`(userRepository.findById(anyString())).thenReturn(Mono.just(user))
        jwtTokenUtil = JwtTokenUtil(jwtProperties, userRepository)
        userService = UserService(userRepository, jwtTokenUtil)
    }

    @DisplayName("Success")
    @Test
    fun success() {
        val user = getMockUser()
        val refreshToken = jwtTokenUtil.generateRefreshToken(user.id!!)
        val requestDto = AccessTokenUpdateRequestDto(refreshToken)
        userService.updateAccessToken(Mono.just(requestDto))
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
        val requestDto = AccessTokenUpdateRequestDto(refreshToken)
        userService.updateAccessToken(Mono.just(requestDto))
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
        `when`(userRepository.findById(anyString())).thenReturn(Mono.empty())
        val refreshToken = jwtTokenUtil.generateRefreshToken(user.id!!)
        val requestDto = AccessTokenUpdateRequestDto(refreshToken)
        userService.updateAccessToken(Mono.just(requestDto))
            .`as`(StepVerifier::create)
            .expectErrorMatches {
                it.message shouldBe "Invalid userId."
                it.shouldBeInstanceOf<AuthenticateException>()
                true
            }.verify()
    }
}
