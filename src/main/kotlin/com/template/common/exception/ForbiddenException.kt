package com.template.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ResponseStatusException

@ResponseStatus(HttpStatus.FORBIDDEN)
abstract class ForbiddenException(message: String) : ApiException(HttpStatus.FORBIDDEN, message)
