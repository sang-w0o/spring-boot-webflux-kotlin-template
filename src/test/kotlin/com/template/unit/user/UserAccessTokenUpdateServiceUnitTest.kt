package com.template.unit.user

import com.template.security.exception.AuthenticateException
import com.template.security.tools.JwtTokenUtil
import com.template.unit.BaseUnitTest
import com.template.user.domain.User
import com.template.user.dto.AccessTokenUpdateRequestDto
import com.template.user.service.UserService
import com.template.util.TestUtils
import com.template.util.TestUtils.JWT_REFRESH_TOKEN_EXP
import org.junit.jupiter.api.*
import org.junit.jupiter.api.Assertions.assertTrue
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

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
                assertEquals(HttpStatus.OK, it.statusCode)
                assertNotNull(it.body!!.accessToken)
                assertDoesNotThrow { jwtTokenUtil.verify(it.body!!.accessToken) }
                true
            }.verifyComplete()
    }

    @DisplayName("Fail - Expired refreshToken")
    @Test
    fun failWithExpiredRefreshToken() {
        val refreshToken = TestUtils.generateExpiredToken(JWT_REFRESH_TOKEN_EXP, jwtProperties.secret)
        val requestDto = AccessTokenUpdateRequestDto(refreshToken)
        userService.updateAccessToken(Mono.just(requestDto))
            .`as`(StepVerifier::create)
            .expectErrorMatches {
                assertEquals("Jwt 토큰이 만료되었습니다.", it.message!!)
                assertTrue(it is AuthenticateException)
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
                assertEquals("Invalid userId.", it.message!!)
                assertTrue(it is AuthenticateException)
                true
            }.verify()
    }
}
