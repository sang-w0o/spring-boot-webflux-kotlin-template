package com.template.integration.user

import com.template.integration.ApiIntegrationTest
import com.template.user.controller.request.UserLoginRequest
import com.template.user.domain.User
import com.template.util.EMAIL
import com.template.util.NAME
import com.template.util.PASSWORD
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

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
        val request = UserLoginRequest(EMAIL, PASSWORD)
        client.post().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(request)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("accessToken").isNotEmpty
            .jsonPath("refreshToken").isNotEmpty
    }

    @DisplayName("Fail - Wrong email")
    @Test
    fun failWithWrongEmail() {
        val request = UserLoginRequest("wrong@email.com", PASSWORD)
        val body = client.post().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(request)
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
        assertErrorResponse(body, API_PATH, ERROR_MESSAGE, HttpStatus.NOT_FOUND)
    }

    @DisplayName("Fail - Wrong password")
    @Test
    fun failWithWrongPassword() {
        val request = UserLoginRequest(EMAIL, "wrongPassword")
        val body = client.post().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(request)
            .exchange()
            .expectStatus().isNotFound
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
        assertErrorResponse(body, API_PATH, ERROR_MESSAGE, HttpStatus.NOT_FOUND)
    }

    @DisplayName("Fail - Empty email")
    @Test
    fun wailWithEmptyEmail() {
        val request = UserLoginRequest("", PASSWORD)
        val body = client.post().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
        assertErrorResponse(body, API_PATH, "email is required.", HttpStatus.BAD_REQUEST)
    }

    @DisplayName("Fail - Wrong email format")
    @Test
    fun failWithWrongEmailFormat() {
        val request = UserLoginRequest("wrongEmailFormat", PASSWORD)
        val body = client.post().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
        assertErrorResponse(body, API_PATH, "wrong email format.", HttpStatus.BAD_REQUEST)
    }

    @DisplayName("Fail - Empty password")
    @Test
    fun failWithEmptyPassword() {
        val request = UserLoginRequest(EMAIL, " ")
        val body = client.post().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(request)
            .exchange()
            .expectStatus().isBadRequest
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
        assertErrorResponse(body, API_PATH, "password is required.", HttpStatus.BAD_REQUEST)
    }
}
