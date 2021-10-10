package com.template.user.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class UserLoginRequestDto(
    @field:Email(message = "wrong email format.")
    @field:NotBlank(message = "email is required.")
    val email: String,

    @field:NotBlank(message = "password is required.")
    val password: String
)
