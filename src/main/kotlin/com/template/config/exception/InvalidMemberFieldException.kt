package com.template.config.exception

import com.template.common.exception.ApiException
import org.springframework.http.HttpStatus

class InvalidMemberFieldException(message: String) : ApiException(HttpStatus.BAD_REQUEST, message)
