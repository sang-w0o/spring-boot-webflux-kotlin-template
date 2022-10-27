package com.template.user.dto

data class UserCreateRequestDto(
    val name: String,
    val password: String,
    val email: String
)
