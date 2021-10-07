package com.template.security.service

import com.template.security.tools.JwtTokenUtil
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.core.Authentication
import org.springframework.stereotype.Component
import reactor.core.publisher.Mono

@Component
class JwtAuthenticationManager(
    private val jwtTokenUtil: JwtTokenUtil
) : ReactiveAuthenticationManager {
    override fun authenticate(authentication: Authentication): Mono<Authentication> {
        println("Managing..")
        return Mono.just(authentication)
            .flatMap { jwtTokenUtil.getAuthentication(it) }
    }
}
