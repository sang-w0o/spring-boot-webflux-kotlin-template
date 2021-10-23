package com.template.unit.user

import com.template.security.tools.JwtTokenUtil
import com.template.unit.BaseUnitTest
import com.template.user.dto.UserLoginRequestDto
import com.template.user.exception.UserLoginException
import com.template.user.service.UserService
import com.template.util.EMAIL
import com.template.util.PASSWORD
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Tag("UserService-login")
class UserLoginServiceUnitTest : BaseUnitTest() {

    companion object {
        private const val ERROR_MESSAGE = "이메일 또는 비밀번호가 잘못되었습니다."
    }

    private lateinit var userService: UserService

    private lateinit var jwtTokenUtil: JwtTokenUtil

    @BeforeEach
    fun setUp() {
        jwtTokenUtil = JwtTokenUtil(jwtProperties, userRepository)
        userService = UserService(userRepository, jwtTokenUtil)
    }

    @DisplayName("Success")
    @Test
    fun success() {
        `when`(userRepository.findByEmailAndPassword(anyString(), anyString())).thenReturn(Mono.just(getMockUser()))
        val requestDto = UserLoginRequestDto(EMAIL, PASSWORD)
        userService.login(Mono.just(requestDto))
            .`as`(StepVerifier::create)
            .expectNextMatches {
                assertEquals(it.statusCode, HttpStatus.OK)
                assertNotNull(it.body!!.accessToken)
                assertNotNull(it.body!!.refreshToken)
                true
            }.verifyComplete()
    }

    @DisplayName("Fail - Wrong email")
    @Test
    fun failWithWrongEmail() {
        `when`(userRepository.findByEmailAndPassword(anyString(), anyString())).thenReturn(Mono.empty())
        val requestDto = UserLoginRequestDto("wrong@email.com", PASSWORD)
        userService.login(Mono.just(requestDto))
            .`as`(StepVerifier::create)
            .expectErrorMatches {
                assertEquals(ERROR_MESSAGE, it.message)
                assertTrue(it is UserLoginException)
                true
            }.verify()
    }

    @DisplayName("Fail - Wrong password")
    @Test
    fun failWithWrongPassword() {
        `when`(userRepository.findByEmailAndPassword(anyString(), anyString())).thenReturn(Mono.empty())
        val requestDto = UserLoginRequestDto(EMAIL, "wrongPassword")
        userService.login(Mono.just(requestDto))
            .`as`(StepVerifier::create)
            .expectErrorMatches {
                assertEquals(ERROR_MESSAGE, it.message)
                assertTrue(it is UserLoginException)
                true
            }.verify()
    }
}
