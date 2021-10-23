package com.template.unit

import com.template.security.tools.JwtProperties
import com.template.user.domain.User
import com.template.user.domain.UserRepository
import com.template.util.*
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.test.context.ConfigDataApplicationContextInitializer
import org.springframework.boot.test.mock.mockito.MockBean
import org.springframework.test.context.ContextConfiguration
import org.springframework.test.context.junit.jupiter.SpringExtension
import java.util.*

@ExtendWith(SpringExtension::class)
@EnableConfigurationProperties(JwtProperties::class)
@ContextConfiguration(initializers = [ConfigDataApplicationContextInitializer::class])
abstract class BaseUnitTest {

    @MockBean
    protected lateinit var userRepository: UserRepository

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

    protected fun generateOtherSignatureToken(exp: Int): String {
        val claims: MutableMap<String, Any> = mutableMapOf()
        claims["userId"] = USER_ID
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + exp))
            .signWith(SignatureAlgorithm.HS256, "Other Signature")
            .compact()
    }

    protected fun generateTokenWithoutUserIdClaim(): String {
        return Jwts.builder()
            .setClaims(mutableMapOf())
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + EXTRA_TIME))
            .signWith(SignatureAlgorithm.HS256, jwtProperties.secret)
            .compact()
    }
}
