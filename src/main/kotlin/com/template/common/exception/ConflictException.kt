package com.template.common.exception

import org.springframework.http.HttpStatus

abstract class ConflictException(message: String) : ApiException(HttpStatus.CONFLICT, message)
