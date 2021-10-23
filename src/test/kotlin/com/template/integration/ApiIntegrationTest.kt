package com.template.integration

import com.template.security.tools.JwtTokenUtil
import com.template.user.domain.User
import com.template.user.domain.UserRepository
import com.template.util.EMAIL
import com.template.util.NAME
import com.template.util.PASSWORD
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
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

    @Autowired
    protected lateinit var jwtTokenUtil: JwtTokenUtil

    @Value("\${jwt.secret}")
    protected lateinit var secret: String

    @Value("\${jwt.accessTokenExp}")
    protected lateinit var accessTokenExp: String

    @AfterEach
    fun tearDown() {
        userRepository.deleteAll().block()
    }

    protected fun generateUser(): User {
        return userRepository.save(User(NAME, EMAIL, PASSWORD)).block()!!
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
