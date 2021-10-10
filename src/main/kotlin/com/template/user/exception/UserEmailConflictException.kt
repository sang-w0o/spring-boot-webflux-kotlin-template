package com.template.user.exception

import com.template.common.exception.ConflictException

class UserEmailConflictException(message: String) : ConflictException(message)
