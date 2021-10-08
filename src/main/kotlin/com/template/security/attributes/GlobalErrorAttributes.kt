package com.template.security.attributes

import com.template.common.exception.ApiException
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class GlobalErrorAttributes : DefaultErrorAttributes() {

    override fun getErrorAttributes(request: ServerRequest, options: ErrorAttributeOptions): MutableMap<String, Any> {
        val map = super.getErrorAttributes(request, options)
        val throwable = getError(request)
        fillErrorAttributes(request, throwable, map)
        return map
    }

    private fun fillErrorAttributes(request: ServerRequest, throwable: Throwable, map: MutableMap<String, Any>) {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        fillCommonAttributes(request, throwable, map)
        if (throwable is ApiException) {
            map["timestamp"] = dateTimeFormatter.format(LocalDateTime.now())
            map["status"] = throwable.httpStatus.value()
            map["error"] = throwable.message!!
        } else {
            map["timestamp"] = dateTimeFormatter.format(LocalDateTime.now())
            map["status"] = HttpStatus.INTERNAL_SERVER_ERROR.value()
            map["error"] = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase
        }
    }

    private fun fillCommonAttributes(request: ServerRequest, throwable: Throwable, map: MutableMap<String, Any>) {
        map["message"] = throwable.message!!
        map["path"] = request.path()
        val optionalRemoteAddress = request.remoteAddress()
        var remoteAddress = "UNKNOWN"
        if (optionalRemoteAddress.isPresent) {
            remoteAddress = optionalRemoteAddress.get().toString()
        }
        map["remote"] = remoteAddress
    }
}
