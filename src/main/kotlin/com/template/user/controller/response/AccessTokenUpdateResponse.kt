package com.template.user.controller.response

import com.template.user.dto.AccessTokenUpdateResponseDto

data class AccessTokenUpdateResponse(
    val accessToken: String = ""
) {
    companion object {
        fun from(dto: AccessTokenUpdateResponseDto) = AccessTokenUpdateResponse(dto.accessToken)
    }
}
