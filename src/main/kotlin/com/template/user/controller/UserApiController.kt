package com.template.user.controller

import com.template.config.annotation.LoggedInUser
import com.template.user.domain.User
import com.template.user.dto.UserCreateRequestDto
import com.template.user.dto.UserInfoResponseDto
import com.template.user.dto.UserLoginRequestDto
import com.template.user.dto.UserLoginResponseDto
import com.template.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
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

    @PostMapping("/login")
    fun login(@RequestBody @Valid requestDto: Mono<UserLoginRequestDto>): Mono<ResponseEntity<UserLoginResponseDto>> {
        return userService.login(requestDto)
    }

    @GetMapping
    fun getUserInfo(@LoggedInUser user: User): Mono<ResponseEntity<UserInfoResponseDto>> {
        return userService.getInfo(Mono.just(user))
    }
}
