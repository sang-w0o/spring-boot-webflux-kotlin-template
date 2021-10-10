package com.template.user.dto

import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class UserCreateRequestDto(
    @field:NotBlank(message = "name is required.")
    val name: String,

    @field:NotBlank(message = "password is required")
    val password: String,

    @field:Email
    @field:NotBlank(message = "email is required.")
    val email: String
)
