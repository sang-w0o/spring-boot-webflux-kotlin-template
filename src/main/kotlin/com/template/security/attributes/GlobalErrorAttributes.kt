package com.template.security.attributes

import com.template.common.exception.ApiException
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.server.ResponseStatusException
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
        fillCommonAttributes(request, throwable, map)
        when(throwable) {
            is ResponseStatusException -> {
                map["status"] = throwable.status.value()
                map["error"] = throwable.message
            }
            is ApiException -> {
                map["status"] = throwable.status.value()
                map["error"] = throwable.message
            }
            else -> {
                map["status"] = HttpStatus.INTERNAL_SERVER_ERROR.value()
                map["error"] = HttpStatus.INTERNAL_SERVER_ERROR.reasonPhrase
            }
        }
    }

    private fun fillCommonAttributes(request: ServerRequest, throwable: Throwable, map: MutableMap<String, Any>) {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        map["timestamp"] = dateTimeFormatter.format(LocalDateTime.now())
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
