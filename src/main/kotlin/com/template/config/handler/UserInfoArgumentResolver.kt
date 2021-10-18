package com.template.config.handler

import com.template.config.annotation.LoggedInUser
import com.template.user.domain.User
import org.springframework.core.MethodParameter
import org.springframework.security.core.context.ReactiveSecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.reactive.BindingContext
import org.springframework.web.reactive.result.method.HandlerMethodArgumentResolver
import org.springframework.web.server.ServerWebExchange
import reactor.core.publisher.Mono
import kotlin.RuntimeException

@Component
class UserInfoArgumentResolver : HandlerMethodArgumentResolver {
    override fun supportsParameter(parameter: MethodParameter): Boolean {
        return parameter.getParameterAnnotation(LoggedInUser::class.java) != null && parameter.parameterType == User::class.java
    }

    override fun resolveArgument(
        parameter: MethodParameter,
        bindingContext: BindingContext,
        exchange: ServerWebExchange
    ): Mono<Any> {
        return ReactiveSecurityContextHolder.getContext().map {
            it.authentication.principal
        }.filter {
            it is User
        }.switchIfEmpty(Mono.error(RuntimeException("Authentication.principle is not type of User")))
            .map {
                it as User
            }
    }
}
