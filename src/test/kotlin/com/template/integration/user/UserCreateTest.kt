package com.template.integration.user

import com.template.integration.ApiIntegrationTest
import com.template.user.domain.User
import com.template.user.dto.UserCreateRequestDto
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

@Tag("[POST]v1/user")
class UserCreateTest : ApiIntegrationTest() {

    companion object {
        const val API_PATH = "/v1/user/signup"
    }

    @DisplayName("Success")
    @Test
    fun success() {
        val requestDto = UserCreateRequestDto(NAME, PASSWORD, EMAIL)
        client.post().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isCreated
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectHeader().location("/v1/user")
            .expectBody()
            .jsonPath("id").isNotEmpty
            .jsonPath("name").isEqualTo(NAME)
            .jsonPath("email").isEqualTo(EMAIL)
    }

    @DisplayName("Fail - Conflicting email")
    @Test
    fun failWithConflictingEmail() {
        userRepository.save(User(NAME, EMAIL, PASSWORD)).block()
        val requestDto = UserCreateRequestDto(NAME, PASSWORD, EMAIL)
        client.post().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("timestamp").isNotEmpty
            .jsonPath("message").isEqualTo("Duplicate email($EMAIL)")
            .jsonPath("remote").isNotEmpty
            .jsonPath("path").isEqualTo(API_PATH)
            .jsonPath("status").isEqualTo(HttpStatus.CONFLICT.value())
    }

    @DisplayName("Fail - empty name")
    @Test
    fun failWithEmptyName() {
        val requestDto = UserCreateRequestDto(" ", PASSWORD, EMAIL)
        client.post().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("timestamp").isNotEmpty
            .jsonPath("message").isEqualTo("name is required.")
            .jsonPath("remote").isNotEmpty
            .jsonPath("path").isEqualTo(API_PATH)
            .jsonPath("status").isEqualTo(HttpStatus.BAD_REQUEST.value())
    }

    @DisplayName("Fail - empty email")
    @Test
    fun failWithEmptyEmail() {
        val requestDto = UserCreateRequestDto(NAME, PASSWORD, " ")
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

    @DisplayName("Fail - Wrong email format")
    @Test
    fun failWithWrongEmailFormat() {
        val requestDto = UserCreateRequestDto(NAME, PASSWORD, "wrongEmailFormat")
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
}
