package com.template.integration.user

import com.template.integration.ApiIntegrationTest
import com.template.user.dto.UserCreateRequestDto
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Tag
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

@Tag("[POST]v1/user")
class UserCreateTest : ApiIntegrationTest() {

    @DisplayName("Success")
    @Test
    fun success() {
        val requestDto = UserCreateRequestDto(NAME, EMAIL, PASSWORD)
        client.post().uri("/v1/user")
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
}