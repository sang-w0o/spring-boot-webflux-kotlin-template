package com.template.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException

abstract class ApiException(status: HttpStatus, reason: String) : ResponseStatusException(status, reason) {
    override val message: String
        get() = reason ?: "No message provided."
}
