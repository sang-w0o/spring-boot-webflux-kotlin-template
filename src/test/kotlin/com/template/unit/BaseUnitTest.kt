package com.template.unit

import com.template.security.tools.JwtProperties
import com.template.user.domain.User
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension

@ExtendWith(SpringExtension::class)
@EnableConfigurationProperties(JwtProperties::class)
@ContextConfiguration(initializers = [ConfigDataApplicationContextInitializer::class])
abstract class BaseUnitTest {
    companion object {
        const val USER_ID = "generatedId"
        const val NAME = "userName"
        const val EMAIL = "email@test.com"
        const val PASSWORD = "testPassword"
        const val JWT_SECRET = "TestJwtSecretKey"
        const val JWT_ACCESS_TOKEN_EXP = 86400000
        const val JWT_REFRESH_TOKEN_EXP = 604800000
    }

    protected var jwtProperties: JwtProperties = JwtProperties()

    init {
        jwtProperties.secret = JWT_SECRET
        jwtProperties.accessTokenExp = JWT_ACCESS_TOKEN_EXP
        jwtProperties.refreshTokenExp = JWT_REFRESH_TOKEN_EXP
    }

    protected fun getMockUser(): User {
        val savedUser = User(NAME, EMAIL, PASSWORD)
        savedUser.id = USER_ID
        return savedUser
    }
}
