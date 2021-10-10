package com.template.unit.user

import com.template.unit.BaseUnitTest
import com.template.user.domain.User
import com.template.user.domain.UserRepository
import com.template.user.dto.UserCreateRequestDto
import com.template.user.exception.UserEmailConflictException
import com.template.user.service.UserService
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.mockito.ArgumentMatchers.any
import org.mockito.ArgumentMatchers.anyString
import org.mockito.Mockito.`when`
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.http.HttpStatus
import reactor.core.publisher.Mono
import reactor.test.StepVerifier
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

@Tag("UserService-create")
class UserCreateServiceUnitTest : BaseUnitTest() {

    @MockBean
    private lateinit var userRepository: UserRepository

    private lateinit var userService: UserService

    @BeforeEach
    fun setUp() {
        userService = UserService(userRepository)
    }

    @DisplayName("Success")
    @Test
    fun success() {
        val savedUser = User(NAME, EMAIL, PASSWORD)
        savedUser.id = "generatedId"
        `when`(userRepository.save(any())).thenReturn(Mono.just(savedUser))
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
