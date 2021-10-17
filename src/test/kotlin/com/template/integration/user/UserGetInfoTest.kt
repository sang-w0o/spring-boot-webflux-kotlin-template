package com.template.integration.user

import com.template.integration.ApiIntegrationTest
import com.template.security.tools.JwtTokenUtil
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import java.util.*

@Tag("[GET]v1/user/info")
class UserGetInfoTest : ApiIntegrationTest() {

    companion object {
        const val API_PATH = "/v1/user/info"
    }

    @Autowired
    private lateinit var jwtTokenUtil: JwtTokenUtil

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
        val userId = generateUser().id!!
        val accessToken = jwtTokenUtil.generateAccessToken("WRONG_USER_ID")
        client.get().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", "Bearer $accessToken")
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("timestamp").isNotEmpty
            .jsonPath("message").isEqualTo("Invalid userId")
            .jsonPath("remote").isNotEmpty
            .jsonPath("path").isEqualTo(API_PATH)
            .jsonPath("status").isEqualTo(HttpStatus.UNAUTHORIZED.value())
    }

    @DisplayName("Fail - No Authorization Header present.")
    @Test
    fun failWithAbsentAuthorizationHeader() {
        client.get().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("timestamp").isNotEmpty
            .jsonPath("message").isEqualTo("JWT Header is missing.")
            .jsonPath("remote").isNotEmpty
            .jsonPath("path").isEqualTo(API_PATH)
            .jsonPath("status").isEqualTo(HttpStatus.UNAUTHORIZED.value())
    }

    @DisplayName("Fail - Wrong Authorization Header scheme.")
    @Test
    fun failWithWrongAuthorizationHeaderScheme() {
        client.get().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", "WrongScheme token")
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("timestamp").isNotEmpty
            .jsonPath("message").isEqualTo("Invalid Authorization header scheme.")
            .jsonPath("remote").isNotEmpty
            .jsonPath("path").isEqualTo(API_PATH)
            .jsonPath("status").isEqualTo(HttpStatus.UNAUTHORIZED.value())
    }

    @DisplayName("Fail - AccessToken malformed.")
    @Test
    fun failWithExpiredAccessToken() {
        client.get().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .header("Authorization", "Bearer token")
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("timestamp").isNotEmpty
            .jsonPath("message").isEqualTo("잘못된 형식의 Jwt 토큰입니다.")
            .jsonPath("remote").isNotEmpty
            .jsonPath("path").isEqualTo(API_PATH)
            .jsonPath("status").isEqualTo(HttpStatus.UNAUTHORIZED.value())
    }
}
