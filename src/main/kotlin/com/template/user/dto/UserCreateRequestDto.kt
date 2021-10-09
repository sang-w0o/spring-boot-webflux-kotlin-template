package com.template.user.dto

import javax.validation.constraints.NotBlank

data class UserCreateRequestDto(
    @field:NotBlank
    val name: String,

    @field:NotBlank
    val password: String,

    @field:NotBlank
    val email: String
)
