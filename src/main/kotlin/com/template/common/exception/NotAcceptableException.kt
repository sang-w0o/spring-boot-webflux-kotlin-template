package com.template.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ResponseStatusException

@ResponseStatus(HttpStatus.NOT_ACCEPTABLE)
abstract class NotAcceptableException(message: String) : ApiException(HttpStatus.NOT_ACCEPTABLE, message)
