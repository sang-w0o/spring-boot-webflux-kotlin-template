package com.template.user.controller.response

import com.template.user.dto.UserInfoResponseDto

data class UserInfoResponse(
    val id: String,
    val name: String,
    val email: String
) {
    companion object {
        fun from(dto: UserInfoResponseDto) = UserInfoResponse(
            dto.id,
            dto.name,
            dto.email
        )
    }
}
