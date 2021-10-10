package com.template.integration.user

import com.template.integration.ApiIntegrationTest
import com.template.user.domain.User
import com.template.user.dto.UserCreateRequestDto
import com.template.user.dto.UserLoginRequestDto
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

@Tag("[POST]v1/user/login")
class UserLoginTest : ApiIntegrationTest() {

    companion object {
        const val API_PATH = "/v1/user/login"
        const val ERROR_MESSAGE = "이메일 또는 비밀번호가 잘못되었습니다."
    }

    @BeforeEach
    fun setUpUserLoginTest() {
        userRepository.save(User(NAME, EMAIL, PASSWORD)).block()
    }

    @DisplayName("Success")
    @Test
    fun success() {
        val requestDto = UserLoginRequestDto(EMAIL, PASSWORD)
        client.post().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("accessToken").isNotEmpty
            .jsonPath("refreshToken").isNotEmpty
    }

    @DisplayName("Fail - Wrong email")
    @Test
    fun failWithWrongEmail() {
        val requestDto = UserLoginRequestDto("wrong@email.com", PASSWORD)
        client.post().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("timestamp").isNotEmpty
            .jsonPath("message").isEqualTo(ERROR_MESSAGE)
            .jsonPath("remote").isNotEmpty
            .jsonPath("path").isEqualTo(API_PATH)
            .jsonPath("status").isEqualTo(HttpStatus.NOT_FOUND.value())
    }

    @DisplayName("Fail - Wrong password")
    @Test
    fun failWithWrongPassword() {
        val requestDto = UserLoginRequestDto(EMAIL, "wrongPassword")
        client.post().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("timestamp").isNotEmpty
            .jsonPath("message").isEqualTo(ERROR_MESSAGE)
            .jsonPath("remote").isNotEmpty
            .jsonPath("path").isEqualTo(API_PATH)
            .jsonPath("status").isEqualTo(HttpStatus.NOT_FOUND.value())
    }

    @DisplayName("Fail - Empty email")
    @Test
    fun wailWithEmptyEmail() {
        val requestDto = UserLoginRequestDto("", PASSWORD)
        client.post().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("timestamp").isNotEmpty
            .jsonPath("message").isEqualTo("email is required.")
            .jsonPath("remote").isNotEmpty
            .jsonPath("path").isEqualTo(API_PATH)
            .jsonPath("status").isEqualTo(HttpStatus.BAD_REQUEST.value())

    }

    @DisplayName("Fail - Wrong email format")
    @Test
    fun failWithWrongEmailFormat() {
        val requestDto = UserLoginRequestDto("wrongEmailFormat", PASSWORD)
        client.post().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("timestamp").isNotEmpty
            .jsonPath("message").isEqualTo("wrong email format.")
            .jsonPath("remote").isNotEmpty
            .jsonPath("path").isEqualTo(API_PATH)
            .jsonPath("status").isEqualTo(HttpStatus.BAD_REQUEST.value())
    }

    @DisplayName("Fail - Empty password")
    @Test
    fun failWithEmptyPassword() {
        val requestDto = UserLoginRequestDto(EMAIL, " ")
        client.post().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("timestamp").isNotEmpty
            .jsonPath("message").isEqualTo("password is required.")
            .jsonPath("remote").isNotEmpty
            .jsonPath("path").isEqualTo(API_PATH)
            .jsonPath("status").isEqualTo(HttpStatus.BAD_REQUEST.value())
    }
}