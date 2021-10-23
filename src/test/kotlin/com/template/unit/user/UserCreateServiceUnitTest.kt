package com.template.unit.user

import com.template.security.tools.JwtTokenUtil
import com.template.unit.BaseUnitTest
import com.template.user.dto.UserCreateRequestDto
import com.template.user.exception.UserEmailConflictException
import com.template.user.service.UserService
import com.template.util.EMAIL
import com.template.util.NAME
import com.template.util.PASSWORD
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.http.HttpStatus
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Tag("UserService-create")
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
        `when`(userRepository.save(any())).thenReturn(Mono.just(getMockUser()))
        `when`(userRepository.existsByEmail(anyString())).thenReturn(Mono.just(false))
        val requestDto = UserCreateRequestDto(NAME, PASSWORD, EMAIL)
        userService.create(Mono.just(requestDto))
            .`as`(StepVerifier::create)
            .expectNextMatches {
                assertEquals(it.statusCode, HttpStatus.CREATED)
                assertEquals("/v1/user", it.headers["location"]?.get(0) ?: "Wrong")
                assertEquals(NAME, it.body!!.name)
                assertEquals(EMAIL, it.body!!.email)
                assertNotNull(it.body!!.id)
                true
            }.verifyComplete()
    }

    @DisplayName("Fail - Conflict email")
    @Test
    fun failWithConflictEmail() {
        `when`(userRepository.existsByEmail(anyString())).thenReturn(Mono.just(true))
        val requestDto = UserCreateRequestDto(NAME, PASSWORD, EMAIL)
        userService.create(Mono.just(requestDto))
            .`as`(StepVerifier::create)
            .expectErrorMatches {
                assertEquals("Duplicate email($EMAIL)", it.message!!)
                assertTrue(it is UserEmailConflictException)
                true
            }
            .verify()
    }
}
