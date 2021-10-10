package com.template.config.handler

import com.template.config.exception.InvalidMemberFieldException
import org.springframework.core.codec.DecodingException
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler

@ControllerAdvice
class RequestBodyDecodingExceptionHandler {

    @ExceptionHandler(DecodingException::class)
    fun handleDecodingException(exception: DecodingException) {
        throw InvalidMemberFieldException("Wrong request body received.")
    }
}