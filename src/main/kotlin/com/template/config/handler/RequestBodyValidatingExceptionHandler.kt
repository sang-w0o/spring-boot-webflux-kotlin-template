package com.template.config.handler

import com.template.config.exception.InvalidMemberFieldException
import org.springframework.context.support.DefaultMessageSourceResolvable
import org.springframework.core.codec.DecodingException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice
import org.springframework.web.bind.support.WebExchangeBindException
import java.util.stream.Collectors

@RestControllerAdvice
class RequestBodyValidatingExceptionHandler {

    @ExceptionHandler(DecodingException::class)
    fun handleDecodingException(exception: DecodingException) {
        throw InvalidMemberFieldException("Wrong request body received.")
    }

    @ExceptionHandler(WebExchangeBindException::class)
    fun handleWebExchangeBindException(exception: WebExchangeBindException) {
        val error = exception
            .bindingResult
            .allErrors
            .stream()
            .map(DefaultMessageSourceResolvable::getDefaultMessage)
            .collect(Collectors.toList())[0]
        throw InvalidMemberFieldException(error ?: "Invalid member field.")
    }
}
