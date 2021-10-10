package com.template.user.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotNull

data class UserLoginRequestDto(
    @field:NotNull(message = "email is required.")
    @field:Email(message = "wrong email format.")
    val email: String,

    @field:NotBlank(message = "password is required")
    val password: String
)
