package com.template.security.service

import com.template.security.exception.AuthenticateException
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
class JwtServerAuthenticationConverter : ServerAuthenticationConverter {
    override fun convert(exchange: ServerWebExchange): Mono<Authentication> {
        return Mono.just(exchange)
            .flatMap { Mono.justOrEmpty(extractTokenFromHeader(it))}
            .map { UsernamePasswordAuthenticationToken(it, it) }
    }

    private fun extractTokenFromHeader(exchange: ServerWebExchange): String {
        val authorizationHeader = exchange.request.headers["Authorization"]
        if(authorizationHeader.isNullOrEmpty()) {
            throw AuthenticateException("JWT Header is missing.")
        } else {
            if(authorizationHeader[0].contains("Bearer")) {
                return authorizationHeader[0].replace("Bearer ", "")
            } else throw AuthenticateException("Invalid Authorization header scheme.")
        }
    }
}