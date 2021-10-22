package com.template.common.exception

import org.springframework.http.HttpStatus

abstract class ForbiddenException(message: String) : ApiException(HttpStatus.FORBIDDEN, message)
