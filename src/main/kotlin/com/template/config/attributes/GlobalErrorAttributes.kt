package com.template.config.attributes

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.web.error.ErrorAttributeOptions
import org.springframework.boot.web.reactive.error.DefaultErrorAttributes
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.server.ResponseStatusException
import java.time.Clock
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Component
class GlobalErrorAttributes : DefaultErrorAttributes() {

    @Autowired
    private lateinit var clock: Clock

    override fun getErrorAttributes(request: ServerRequest, options: ErrorAttributeOptions): MutableMap<String, Any> {
        val map = mutableMapOf<String, Any>()
        val throwable = getError(request)
        fillErrorAttributes(request, throwable, map)
        return map
    }

    private fun fillErrorAttributes(request: ServerRequest, throwable: Throwable, map: MutableMap<String, Any>) {
        fillCommonAttributes(request, throwable, map)
        when (throwable) {
            is ResponseStatusException -> {
                map["status"] = throwable.status.value()
            }
            else -> {
                map["status"] = HttpStatus.INTERNAL_SERVER_ERROR.value()
            }
        }
    }

    private fun fillCommonAttributes(request: ServerRequest, throwable: Throwable, map: MutableMap<String, Any>) {
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        map["timestamp"] = dateTimeFormatter.format(LocalDateTime.now(clock))
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
