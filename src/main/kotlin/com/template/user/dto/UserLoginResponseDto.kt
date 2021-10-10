package com.template.user.dto

data class UserLoginResponseDto(
    val accessToken: String,
    val refreshToken: String
)
