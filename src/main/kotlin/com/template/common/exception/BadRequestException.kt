package com.template.common.exception

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.server.ResponseStatusException

@ResponseStatus(HttpStatus.BAD_REQUEST)
abstract class BadRequestException(message: String) : ResponseStatusException(HttpStatus.BAD_REQUEST, message)
