package com.template.integration

import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Test
import org.springframework.http.MediaType

class ActuatorTest : ApiIntegrationTest() {

    @DisplayName("Actuator - Health Check")
    @Test
    fun healthCheckApiIsOpen() {
        client.get().uri("/actuator/health")
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("status").isEqualTo("UP")
    }

    @DisplayName("Actuator - Information")
    @Test
    fun infoApiIsOpen() {
        client.get().uri("/actuator/info")
            .header("Accept", MediaType.APPLICATION_JSON_VALUE)
            .exchange()
            .expectStatus().isOk
            .expectHeader().contentType(MediaType.APPLICATION_JSON)
            .expectBody()
            .jsonPath("application.author").exists()
            .jsonPath("application.version").exists()
            .jsonPath("application.description").exists()
            .jsonPath("application.more_info").exists()
    }
}
