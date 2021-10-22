package com.template.integration.user

import com.template.integration.ApiIntegrationTest
import com.template.util.TestUtils.EMAIL
import com.template.util.TestUtils.NAME
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

@Tag("[GET]v1/user/info")
class UserGetInfoTest : ApiIntegrationTest() {

    companion object {
        const val API_PATH = "/v1/user/info"
    }

    @DisplayName("Success")
    @Test
    fun success() {
        val userId = generateUser().id!!
        val accessToken = jwtTokenUtil.generateAccessToken(userId)
        client.get().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", "Bearer $accessToken")
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("id").isEqualTo(userId)
            .jsonPath("name").isEqualTo(NAME)
            .jsonPath("email").isEqualTo(EMAIL)
    }

    @DisplayName("Fail - Invalid userId")
    @Test
    fun failWithInvalidUserId() {
        val accessToken = jwtTokenUtil.generateAccessToken("WRONG_USER_ID")
        val body = client.get().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", "Bearer $accessToken")
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
        assertErrorResponse(body, API_PATH, "Invalid userId", HttpStatus.UNAUTHORIZED)
    }

    @DisplayName("Fail - No Authorization Header present.")
    @Test
    fun failWithAbsentAuthorizationHeader() {
        val body = client.get().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
        assertErrorResponse(body, API_PATH, "JWT Header is missing.", HttpStatus.UNAUTHORIZED)
    }

    @DisplayName("Fail - Wrong Authorization Header scheme.")
    @Test
    fun failWithWrongAuthorizationHeaderScheme() {
        val body = client.get().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", "WrongScheme token")
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
        assertErrorResponse(body, API_PATH, "Invalid Authorization header scheme.", HttpStatus.UNAUTHORIZED)
    }

    @DisplayName("Fail - AccessToken malformed.")
    @Test
    fun failWithExpiredAccessToken() {
        val body = client.get().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", "Bearer token")
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
        assertErrorResponse(body, API_PATH, "잘못된 형식의 Jwt 토큰입니다.", HttpStatus.UNAUTHORIZED)
    }
}
