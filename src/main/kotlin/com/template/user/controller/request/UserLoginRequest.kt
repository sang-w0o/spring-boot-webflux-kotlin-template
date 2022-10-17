package com.template.user.controller.request

import com.template.user.dto.UserLoginRequestDto
import javax.validation.constraints.Email
import javax.validation.constraints.NotBlank

data class UserLoginRequest(
    @field:Email(message = "wrong email format.")
    @field:NotBlank(message = "email is required.")
    val email: String,

    @field:NotBlank(message = "password is required.")
    val password: String
) {
    fun toDto() = UserLoginRequestDto(email, password)
}
