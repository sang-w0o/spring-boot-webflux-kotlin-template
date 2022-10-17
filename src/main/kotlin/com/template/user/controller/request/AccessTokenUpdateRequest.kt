package com.template.user.controller.request

import javax.validation.constraints.NotBlank

data class AccessTokenUpdateRequest(
    @field:NotBlank(message = "refreshToken is required.")
    val refreshToken: String = ""
)
