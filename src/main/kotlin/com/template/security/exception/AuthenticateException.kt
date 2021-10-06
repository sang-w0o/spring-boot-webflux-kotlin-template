package com.template.security.exception

import org.springframework.security.core.AuthenticationException

class AuthenticateException(message: String) : AuthenticationException(message)
