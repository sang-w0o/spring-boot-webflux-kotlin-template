package com.template.user.controller.response

import com.template.user.dto.UserLoginResponseDto

data class UserLoginResponse(
    val accessToken: String,
    val refreshToken: String
) {
    companion object {
        fun from(dto: UserLoginResponseDto) = UserLoginResponse(
            dto.accessToken,
            dto.refreshToken
        )
    }
}
