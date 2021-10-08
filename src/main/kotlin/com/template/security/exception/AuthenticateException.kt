package com.template.security.exception

import com.template.common.exception.UnauthorizedException

class AuthenticateException(message: String) : UnauthorizedException(message)
