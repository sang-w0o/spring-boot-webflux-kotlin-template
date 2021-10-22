package com.template.user.dto

import javax.validation.constraints.NotBlank

data class AccessTokenUpdateRequestDto(
    @field:NotBlank(message = "refreshToken is required.")
    val refreshToken: String = ""
)
