package com.template.integration.user

import com.template.integration.ApiIntegrationTest
import com.template.user.dto.AccessTokenUpdateRequestDto
import com.template.util.generateExpiredToken
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType

class UserAccessTokenUpdateTest : ApiIntegrationTest() {

    companion object {
        const val API_PATH = "/v1/user/update-token"
    }

    @DisplayName("Success")
    @Test
    fun success() {
        val userId = generateUser().id!!
        val refreshToken = jwtTokenUtil.generateRefreshToken(userId)
        val requestDto = AccessTokenUpdateRequestDto(refreshToken)
        client.post().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("accessToken").isNotEmpty
    }

    @DisplayName("Fail - Invalid userId")
    @Test
    fun failWithInvalidUserId() {
        val refreshToken = jwtTokenUtil.generateRefreshToken("WRONG_USER_ID")
        val requestDto = AccessTokenUpdateRequestDto(refreshToken)
        val body = client.post().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
        assertErrorResponse(body, API_PATH, "Invalid userId.", HttpStatus.UNAUTHORIZED)
    }

    @DisplayName("Fail - Expired refreshToken")
    @Test
    fun failWithExpiredRefreshToken() {
        val refreshToken = generateExpiredToken(Integer.valueOf(accessTokenExp), secret)
        val requestDto = AccessTokenUpdateRequestDto(refreshToken)
        val body = client.post().uri(API_PATH)
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .bodyValue(requestDto)
            .exchange()
            .expectStatus().isUnauthorized
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
        assertErrorResponse(body, API_PATH, "Jwt 토큰이 만료되었습니다.", HttpStatus.UNAUTHORIZED)
    }
}
