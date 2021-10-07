package com.template.security.service

import com.template.security.exception.AuthenticateException
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.security.access.AccessDeniedException
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono

@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
class AccessDeniedHandler : ServerAccessDeniedHandler {
    override fun handle(exchange: ServerWebExchange, denied: AccessDeniedException): Mono<Void> {
        println("Access Denied..")
        throw AuthenticateException("Access denied.")
    }
}