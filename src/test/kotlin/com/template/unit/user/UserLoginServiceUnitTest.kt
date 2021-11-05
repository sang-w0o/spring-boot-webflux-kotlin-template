package com.template.unit.user

import com.template.security.tools.JwtTokenUtil
import com.template.unit.BaseUnitTest
import com.template.user.dto.UserLoginRequestDto
import com.template.user.exception.UserLoginException
import com.template.user.service.UserService
import com.template.util.EMAIL
import com.template.util.PASSWORD
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.types.shouldBeInstanceOf
import io.mockk.every
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class UserLoginServiceUnitTest : BaseUnitTest() {

    companion object {
        private const val ERROR_MESSAGE = "이메일 또는 비밀번호가 잘못되었습니다."
    }

    private lateinit var userService: UserService

    private lateinit var jwtTokenUtil: JwtTokenUtil

    @BeforeEach
    fun setUp() {
        every { userRepository.findByEmailAndPassword(any(), any()) } returns Mono.empty()
        jwtTokenUtil = JwtTokenUtil(jwtProperties, userRepository)
        userService = UserService(userRepository, jwtTokenUtil)
    }

    @DisplayName("Success")
    @Test
    fun success() {
        every { userRepository.findByEmailAndPassword(any(), any()) } returns Mono.just(getMockUser())
        val requestDto = UserLoginRequestDto(EMAIL, PASSWORD)
        userService.login(Mono.just(requestDto))
            .`as`(StepVerifier::create)
            .expectNextMatches {
                it.statusCode shouldBe HttpStatus.OK
                it.body!!.accessToken shouldNotBe null
                it.body!!.refreshToken shouldNotBe null
                true
            }.verifyComplete()
    }

    @DisplayName("Fail - Wrong email")
    @Test
    fun failWithWrongEmail() {
        val requestDto = UserLoginRequestDto("wrong@email.com", PASSWORD)
        userService.login(Mono.just(requestDto))
            .`as`(StepVerifier::create)
            .expectErrorMatches {
                it.message shouldBe ERROR_MESSAGE
                it.shouldBeInstanceOf<UserLoginException>()
                true
            }.verify()
    }

    @DisplayName("Fail - Wrong password")
    @Test
    fun failWithWrongPassword() {
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
