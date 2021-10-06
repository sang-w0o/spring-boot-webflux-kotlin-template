package com.template.security.tools

import com.template.security.exception.AuthenticateException
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.MalformedJwtException
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.SignatureException
import io.jsonwebtoken.UnsupportedJwtException
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono
import java.util.Date
import java.util.function.Function

@Component
class JwtTokenUtil(private val jwtProperties: JwtProperties) {
    private fun getUserId(claim: Claims): Int {
        try {
            return claim.get("userId", Int::class.javaObjectType)
        } catch (e: Exception) {
            throw AuthenticateException("JWT Claim에 userId가 없습니다.")
        }
    }

    private fun extractExp(token: String): Date {
        return extractClaim(token, Claims::getExpiration)
    }

    private fun extractAllClaims(token: String): Claims {
        try {
            return Jwts.parser().setSigningKey(jwtProperties.secret).parseClaimsJws(token).body
        } catch (expiredJwtException: ExpiredJwtException) {
            throw AuthenticateException("Jwt 토큰이 만료되었습니다.")
        } catch (unsupportedJwtException: UnsupportedJwtException) {
            throw AuthenticateException("지원되지 않는 Jwt 토큰입니다.")
        } catch (malformedJwtException: MalformedJwtException) {
            throw AuthenticateException("잘못된 형식의 Jwt 토큰입니다.")
        } catch (signatureException: SignatureException) {
            throw AuthenticateException("Jwt Signature이 잘못된 값입니다.")
        } catch (illegalArgumentException: IllegalArgumentException) {
            throw AuthenticateException("Jwt 헤더 값이 잘못되었습니다.")
        }
    }

    private fun createToken(claims: Map<String, Any>, exp: Int): String {
        return Jwts.builder()
            .setClaims(claims)
            .setIssuedAt(Date(System.currentTimeMillis()))
            .setExpiration(Date(System.currentTimeMillis() + exp))
            .signWith(SignatureAlgorithm.HS256, jwtProperties.secret)
            .compact()
    }

    private fun <T> extractClaim(token: String, claimResolver: Function<Claims, T>): T {
        return claimResolver.apply(extractAllClaims(token))
    }

    fun extractUserId(token: String): Int {
        return extractClaim(token, this::getUserId)
    }

    fun isTokenExpired(token: String): Boolean {
        return extractExp(token).before(Date())
    }

    fun verify(token: String) {
        extractAllClaims(token)
    }

    fun generateAccessToken(userId: Int): String {
        val claims: MutableMap<String, Any> = mutableMapOf()
        claims["userId"] = userId
        return createToken(claims, jwtProperties.accessTokenExp)
    }

    fun generateRefreshToken(userId: Int): String {
        val claims: MutableMap<String, Any> = mutableMapOf()
        claims["userId"] = userId
        return createToken(claims, jwtProperties.refreshTokenExp)
    }

    fun getAuthentication(authentication: Authentication): Mono<Authentication> {
        val token = authentication.credentials.toString()
        verify(token)
        return Mono.just(authentication)
    }
}
