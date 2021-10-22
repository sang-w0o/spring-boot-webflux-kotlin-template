package com.template.common.exception

import org.springframework.http.HttpStatus

abstract class NotAcceptableException(message: String) : ApiException(HttpStatus.NOT_ACCEPTABLE, message)
