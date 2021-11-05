package com.template.unit.user

import com.template.security.tools.JwtTokenUtil
import com.template.unit.BaseUnitTest
import com.template.user.dto.UserCreateRequestDto
import com.template.user.exception.UserEmailConflictException
import com.template.user.service.UserService
import com.template.util.EMAIL
import com.template.util.NAME
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

class UserCreateServiceUnitTest : BaseUnitTest() {

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
        every { userRepository.save(any()) } returns Mono.just(getMockUser())
        every { userRepository.existsByEmail(any()) } returns Mono.just(false)
        val requestDto = UserCreateRequestDto(NAME, PASSWORD, EMAIL)
        userService.create(Mono.just(requestDto))
            .`as`(StepVerifier::create)
            .expectNextMatches {
                it.statusCode shouldBe HttpStatus.CREATED
                it.headers["location"]!![0] shouldBe "/v1/user"
                it.body!!.name shouldBe NAME
                it.body!!.email shouldBe EMAIL
                it.body!!.id shouldNotBe null
                true
            }.verifyComplete()
    }

    @DisplayName("Fail - Conflict email")
    @Test
    fun failWithConflictEmail() {
        every { userRepository.existsByEmail(any()) } returns Mono.just(true)
        val requestDto = UserCreateRequestDto(NAME, PASSWORD, EMAIL)
        userService.create(Mono.just(requestDto))
            .`as`(StepVerifier::create)
            .expectErrorMatches {
                it.message shouldBe "Duplicate email($EMAIL)"
                it.shouldBeInstanceOf<UserEmailConflictException>()
                true
            }
            .verify()
    }
}
