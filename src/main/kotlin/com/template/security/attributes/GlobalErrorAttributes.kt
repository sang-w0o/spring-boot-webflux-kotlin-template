package com.template.security.attributes

import com.template.common.exception.ApiException
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import java.time.LocalDateTime

@Component
class GlobalErrorAttributes : DefaultErrorAttributes() {

    override fun getErrorAttributes(request: ServerRequest, options: ErrorAttributeOptions): MutableMap<String, Any> {
        println("GET ERROR ATTRIBUTES")
        val map = super.getErrorAttributes(request, options)
        val throwable = getError(request)
        fillErrorAttributes(request, throwable, map)
        return map
    }

    private fun fillErrorAttributes(request: ServerRequest, throwable: Throwable, map: MutableMap<String, Any>) {
        println("FILL ERROR ATTRIBUTES")
        if (throwable is ApiException) {
            map["timestamp"] = LocalDateTime.now()
            map["status"] = throwable.httpStatus.value()
            map["error"] = throwable.message!!
            map["message"] = throwable.message!!
            map["path"] = request.path()
            map["remote"] = request.remoteAddress().toString()
        } else {
            map["timestamp"] = LocalDateTime.now()
            map["status"] = HttpStatus.INTERNAL_SERVER_ERROR.value()
            map["error"] = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase
            map["message"] = throwable.message!!
            map["path"] = request.path()
            map["remote"] = request.remoteAddress().toString()
        }
    }
}