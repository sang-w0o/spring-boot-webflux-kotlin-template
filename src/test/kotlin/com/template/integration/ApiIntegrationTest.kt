package com.template.integration

import com.template.user.domain.UserRepository
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Bean
import org.springframework.stereotype.Component
import org.springframework.test.context.ActiveProfiles
import org.springframework.test.web.reactive.server.WebTestClient
import reactor.blockhound.BlockHound
import java.io.FileInputStream

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
        userRepository.deleteAll()
    }

    companion object {
        const val NAME = "userName"
        const val EMAIL = "email@test.com"
        const val PASSWORD = "testPassword"
    }
}
