package com.template.common.exception

import org.springframework.http.HttpStatus

abstract class BadRequestException(message: String) : ApiException(HttpStatus.BAD_REQUEST, message)
