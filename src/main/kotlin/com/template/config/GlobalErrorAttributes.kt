package com.template.config

import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest

// TODO: This component is used in global exception handler.
@Component
class GlobalErrorAttributes : DefaultErrorAttributes() {

    val httpStatus: HttpStatus = HttpStatus.BAD_REQUEST
    val message: String = "Error occurred."

    override fun getErrorAttributes(request: ServerRequest, options: ErrorAttributeOptions): MutableMap<String, Any> {
        val map =  super.getErrorAttributes(request, options)
        map["status"] = httpStatus
        map["message"] = message
        return map
    }
}