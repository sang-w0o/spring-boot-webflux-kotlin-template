package com.template.common.exception

import org.springframework.http.HttpStatus

abstract class UnauthorizedException(message: String) : ApiException(HttpStatus.UNAUTHORIZED, message)
