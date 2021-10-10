package com.template.user.controller

import com.template.user.dto.UserCreateRequestDto
import com.template.user.dto.UserInfoResponseDto
import com.template.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Mono
import javax.validation.Valid

@RestController
@RequestMapping("/v1/user")
class UserApiController(
    private val userService: UserService
) {

    @PostMapping
    fun createUser(@RequestBody @Valid requestDto: Mono<UserCreateRequestDto>): Mono<ResponseEntity<UserInfoResponseDto>> {
        return userService.create(requestDto)
    }
}
