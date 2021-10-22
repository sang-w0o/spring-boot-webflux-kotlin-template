package com.template.util

import com.template.unit.BaseUnitTest
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import java.util.*

object TestUtils {

    fun generateExpiredToken(exp: Int, secret: String): String {
        val realExp = BaseUnitTest.EXTRA_TIME + exp
        val claims: MutableMap<String, Any> = mutableMapOf()
        claims["userId"] = BaseUnitTest.USER_ID
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date(System.currentTimeMillis() - realExp))
            .setExpiration(Date(System.currentTimeMillis() - BaseUnitTest.EXTRA_TIME))
            .signWith(SignatureAlgorithm.HS256, secret)
            .compact()
    }
}
