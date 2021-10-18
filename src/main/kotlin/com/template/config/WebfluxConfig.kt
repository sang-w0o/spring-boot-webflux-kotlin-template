package com.template.config

import com.template.config.handler.UserInfoArgumentResolver
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.config.WebFluxConfigurer
import org.springframework.web.reactive.result.method.annotation.ArgumentResolverConfigurer

@Configuration
class WebfluxConfig(val userInfoArgumentResolver: UserInfoArgumentResolver) : WebFluxConfigurer {
    override fun configureArgumentResolvers(configurer: ArgumentResolverConfigurer) {
        configurer.addCustomResolver(userInfoArgumentResolver)
    }
}
