package com.template.integration

import com.template.user.domain.User
import com.template.user.domain.UserRepository
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient

@SpringBootTest
@ActiveProfiles("test")
@AutoConfigureWebTestClient
abstract class ApiIntegrationTest {

    @Autowired
    protected lateinit var client: WebTestClient

    @Autowired
    protected lateinit var userRepository: UserRepository

    @AfterEach
    fun tearDown() {
        userRepository.deleteAll().block()
    }

    protected fun generateUser(): User {
        return userRepository.save(User(NAME, EMAIL, PASSWORD)).block()!!
    }

    companion object {
        const val NAME = "userName"
        const val EMAIL = "email@test.com"
        const val PASSWORD = "testPassword"
    }

    protected fun assertErrorResponse(body: WebTestClient.BodyContentSpec, apiPath: String, message: String, httpStatus: HttpStatus) {
        body
            .jsonPath("timestamp").isNotEmpty
            .jsonPath("message").isEqualTo(message)
            .jsonPath("remote").isNotEmpty
            .jsonPath("path").isEqualTo(apiPath)
            .jsonPath("status").isEqualTo(httpStatus.value())
    }
}
