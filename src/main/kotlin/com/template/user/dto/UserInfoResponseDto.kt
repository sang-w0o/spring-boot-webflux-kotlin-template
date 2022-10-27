package com.template.user.dto

import com.template.user.domain.User

data class UserInfoResponseDto(
    val id: String,
    val name: String,
    val email: String
) {
    companion object {
        fun from(user: User) = UserInfoResponseDto(
            user.id!!,
            user.name,
            user.email
        )
    }
}
