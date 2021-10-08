package com.template.config

import com.template.security.attributes.GlobalErrorAttributes
import org.slf4j.LoggerFactory
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.*
import org.springframework.web.server.ResponseStatusException
import reactor.core.publisher.Mono

@Component
@Order(-2)
class GlobalExceptionHandler(
    errorAttributes: GlobalErrorAttributes,
    applicationContext: ApplicationContext,
    configurer: ServerCodecConfigurer
) : AbstractErrorWebExceptionHandler(
    errorAttributes, WebProperties.Resources(), applicationContext
) {
    private val logger = LoggerFactory.getLogger(GlobalExceptionHandler::class.java)
    init {
        this.setMessageReaders(configurer.readers)
        this.setMessageWriters(configurer.writers)
    }

    override fun getRoutingFunction(errorAttributes: ErrorAttributes): RouterFunction<ServerResponse> {
        return RouterFunctions.route(RequestPredicates.all(), this::createErrorResponse)
    }

    private fun createErrorResponse(request: ServerRequest): Mono<ServerResponse> {
        val throwable = getError(request)
        println(throwable.javaClass.simpleName)
        var status = HttpStatus.INTERNAL_SERVER_ERROR
        if (throwable is ResponseStatusException) { status = throwable.status } else logger.error(throwable.stackTraceToString())
        val errorPropertiesMap = getErrorAttributes(request, ErrorAttributeOptions.defaults())
        return ServerResponse
            .status(status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(errorPropertiesMap))
    }
}
