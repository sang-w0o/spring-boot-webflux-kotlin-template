package com.template.integration.user

import com.template.integration.ApiIntegrationTest
import com.template.user.controller.request.UserCreateRequest
import com.template.user.domain.User
import com.template.util.EMAIL
import com.template.util.NAME
import com.template.util.PASSWORD
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

class UserCreateTest : ApiIntegrationTest() {

    companion object {
        const val API_PATH = "/v1/user/signup"
    }

    @DisplayName("Success")
    @Test
    fun success() {
        val request = UserCreateRequest(NAME, PASSWORD, EMAIL)
        client.post().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(request)
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
        val request = UserCreateRequest(NAME, PASSWORD, EMAIL)
        val body = client.post().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(request)
            .exchange()
            .expectStatus().isEqualTo(HttpStatus.CONFLICT)
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
        assertErrorResponse(body, API_PATH, "Duplicate email($EMAIL)", HttpStatus.CONFLICT)
    }

    @DisplayName("Fail - empty name")
    @Test
    fun failWithEmptyName() {
        val request = UserCreateRequest(" ", PASSWORD, EMAIL)
        val body = client.post().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
        assertErrorResponse(body, API_PATH, "name is required.", HttpStatus.BAD_REQUEST)
    }

    @DisplayName("Fail - empty email")
    @Test
    fun failWithEmptyEmail() {
        val request = UserCreateRequest(NAME, PASSWORD, " ")
        val body = client.post().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
        assertErrorResponse(body, API_PATH, "wrong email format.", HttpStatus.BAD_REQUEST)
    }

    @DisplayName("Fail - Wrong email format")
    @Test
    fun failWithWrongEmailFormat() {
        val request = UserCreateRequest(NAME, PASSWORD, "wrongEmailFormat")
        val body = client.post().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
        assertErrorResponse(body, API_PATH, "wrong email format.", HttpStatus.BAD_REQUEST)
    }
}
