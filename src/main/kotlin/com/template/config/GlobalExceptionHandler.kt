package com.template.config

import com.template.common.exception.ApiException
import com.template.security.attributes.GlobalErrorAttributes
import com.template.security.dto.ErrorResponseDto
import com.template.security.exception.AuthenticateException
import org.springframework.boot.autoconfigure.web.WebProperties
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.ErrorAttributes
import org.springframework.context.ApplicationContext
import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.http.codec.HttpMessageWriter
import org.springframework.http.codec.ServerCodecConfigurer
import org.springframework.http.server.reactive.ServerHttpRequest
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters
import org.springframework.web.reactive.function.server.*
import org.springframework.web.reactive.result.view.ViewResolver
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebExceptionHandler
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import java.time.LocalDateTime

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class GlobalExceptionHandler(
    errorAttributes: GlobalErrorAttributes,
//    resources: WebProperties.Resources,
    applicationContext: ApplicationContext,
    configurer: ServerCodecConfigurer
) : AbstractErrorWebExceptionHandler(
    errorAttributes, WebProperties.Resources(), applicationContext
) {
    init {
        this.setMessageReaders(configurer.readers)
        this.setMessageWriters(configurer.writers)
    }

    override fun getRoutingFunction(errorAttributes: ErrorAttributes): RouterFunction<ServerResponse> {
        println("GET ROUTING FUNCTION")
        return RouterFunctions.route(RequestPredicates.all(), this::createErrorResponse)
    }


//    companion object {
//        class HandlerStrategiesResponseContext(private val strategies: HandlerStrategies) : ServerResponse.Context {
//            override fun messageWriters(): MutableList<HttpMessageWriter<*>> = this.strategies.messageWriters()
//            override fun viewResolvers(): MutableList<ViewResolver> = this.strategies.viewResolvers()
//        }

//    }

//    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
//        println("HANDLING!!")
//        return createErrorResponse(exchange.request as ServerRequest)
//            .flatMap { it.writeTo(exchange, HandlerStrategiesResponseContext(HandlerStrategies.withDefaults())) }
//            .flatMap { Mono.empty() }
//    }

    private fun createErrorResponse(request: ServerRequest): Mono<ServerResponse> {
        val throwable = getError(request)
        println("CREATE ERROR RESPONSE")
        var status = HttpStatus.INTERNAL_SERVER_ERROR
        if(throwable is ApiException) status = throwable.httpStatus
        val errorPropertiesMap = getErrorAttributes(request, ErrorAttributeOptions.defaults())
        return ServerResponse
            .status(status)
            .contentType(MediaType.APPLICATION_JSON)
            .body(BodyInserters.fromValue(errorPropertiesMap))
    }
}

//@Component
//@Order(Ordered.HIGHEST_PRECEDENCE)
//class GlobalExceptionHandler : WebExceptionHandler {
//
//    companion object {
//                class HandlerStrategiesResponseContext(private val strategies: HandlerStrategies) : ServerResponse.Context {
//            override fun messageWriters(): MutableList<HttpMessageWriter<*>> = this.strategies.messageWriters()
//            override fun viewResolvers(): MutableList<ViewResolver> = this.strategies.viewResolvers()
//        }
//    }
//
//    override fun handle(exchange: ServerWebExchange, ex: Throwable): Mono<Void> {
//        println("HANDLE!!")
//        return createErrorResponse(exchange.request, ex)
//            .flatMap { it.writeTo(exchange, HandlerStrategiesResponseContext(HandlerStrategies.withDefaults()))}
//            .flatMap { Mono.empty()}
//    }
//
//    private fun createErrorResponse(request: ServerHttpRequest, throwable: Throwable): Mono<ServerResponse> {
//        println("CREATE ERROR RESPONSE")
//        var status = HttpStatus.INTERNAL_SERVER_ERROR
//        if(throwable is ApiException) status = throwable.httpStatus
//        return ServerResponse
//            .status(status)
//            .contentType(MediaType.APPLICATION_JSON)
//            .bodyValue(getErrorResponseDto(request, throwable))
//    }
//
//    private fun getErrorResponseDto(request: ServerHttpRequest, throwable: Throwable): ErrorResponseDto {
//        return if(throwable is ApiException) {
//            ErrorResponseDto(LocalDateTime.now(), throwable.httpStatus.value(), throwable.message ?: "No message provided.", request.path.toString(), request.remoteAddress.toString())
//        } else ErrorResponseDto(LocalDateTime.now(), HttpStatus.INTERNAL_SERVER_ERROR.value(), throwable.message ?: "No message provided.", request.path.toString(), request.toString())
//    }
//
//}