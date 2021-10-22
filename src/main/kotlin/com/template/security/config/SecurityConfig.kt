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
import org.springframework.security.web.server.util.matcher.NegatedServerWebExchangeMatcher
import org.springframework.security.web.server.util.matcher.ServerWebExchangeMatchers

@EnableWebFluxSecurity
class SecurityConfig(
    private val jwtAuthenticationConverter: ServerAuthenticationConverter,
    private val jwtAuthenticationManager: ReactiveAuthenticationManager,
) {
    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        val jwtAuthenticationWebFilter = AuthenticationWebFilter(jwtAuthenticationManager)
        jwtAuthenticationWebFilter.setServerAuthenticationConverter(jwtAuthenticationConverter)
        return http {
            securityMatcher(
                NegatedServerWebExchangeMatcher(ServerWebExchangeMatchers.pathMatchers("/actuator/**", "/v1/user/signup", "/v1/user/login", "/v1/user/update-token"))
            )
            authorizeExchange { authorize(anyExchange, permitAll) }
            httpBasic { disable() }
            formLogin { disable() }
            csrf { disable() }
            logout { disable() }
            authorizeExchange {
                authorize("/v1/**", authenticated)
            }
            addFilterAt(jwtAuthenticationWebFilter, SecurityWebFiltersOrder.AUTHENTICATION)
        }
    }
}
