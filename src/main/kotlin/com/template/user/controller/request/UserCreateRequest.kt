package com.template.user.controller.request

import com.template.user.dto.UserCreateRequestDto
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank
import javax.validation.constraints.NotEmpty

data class UserCreateRequest(
    @field:NotBlank(message = "name is required.")
    val name: String,

    @field:NotBlank(message = "password is required")
    val password: String,

    @field:Email(message = "wrong email format.")
    @field:NotEmpty(message = "email is required.")
    val email: String
) {
    fun toDto() = UserCreateRequestDto(
        name = name,
        password = password,
        email = email
    )
}
