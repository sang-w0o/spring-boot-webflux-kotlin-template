package com.template.common.exception

import org.springframework.http.HttpStatus

abstract class NotFoundException(message: String) : ApiException(HttpStatus.NOT_FOUND, message)
