package com.template.integration

import com.template.user.domain.User
import com.template.user.domain.UserRepository
import org.junit.jupiter.api.AfterEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
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
}
