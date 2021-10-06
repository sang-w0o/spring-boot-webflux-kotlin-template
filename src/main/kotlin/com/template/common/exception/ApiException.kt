package com.template.common.exception

import org.springframework.http.HttpStatus

abstract class ApiException(message: String, val httpStatus: HttpStatus) : RuntimeException(message)
