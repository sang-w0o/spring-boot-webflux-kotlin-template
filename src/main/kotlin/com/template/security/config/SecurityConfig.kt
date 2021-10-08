package com.template.security.config

import org.springframework.context.annotation.Bean
import org.springframework.security.authentication.ReactiveAuthenticationManager
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.SecurityWebFiltersOrder
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.config.web.server.invoke
import org.springframework.security.web.server.SecurityWebFilterChain
import org.springframework.security.web.server.authentication.AuthenticationWebFilter
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler

@EnableWebFluxSecurity
class SecurityConfig(
    private val jwtAuthenticationConverter: ServerAuthenticationConverter,
    private val jwtAuthenticationManager: ReactiveAuthenticationManager,
    private val serverAccessDeniedHandler: ServerAccessDeniedHandler
) {
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        val jwtAuthenticationWebFilter = AuthenticationWebFilter(jwtAuthenticationManager)
        jwtAuthenticationWebFilter.setServerAuthenticationConverter(jwtAuthenticationConverter)
        return http {
            httpBasic { disable() }
            formLogin { disable() }
            csrf { disable() }
            logout { disable() }
            authorizeExchange {
                authorize("/v1/**", authenticated)
            }
            addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
            exceptionHandling {
                accessDeniedHandler = serverAccessDeniedHandler
            }
        }
    }
}
