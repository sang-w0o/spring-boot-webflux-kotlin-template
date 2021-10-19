package com.template.user.controller

import com.template.config.annotation.LoggedInUser
import com.template.user.domain.User
import com.template.user.dto.*
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

    @PostMapping("/signup")
    fun createUser(@RequestBody @Valid requestDto: Mono<UserCreateRequestDto>): Mono<ResponseEntity<UserInfoResponseDto>> {
        return userService.create(requestDto)
    }

    @PostMapping("/login")
    fun login(@RequestBody @Valid requestDto: Mono<UserLoginRequestDto>): Mono<ResponseEntity<UserLoginResponseDto>> {
        return userService.login(requestDto)
    }

    @GetMapping("/info")
    fun getUserInfo(@LoggedInUser user: User): Mono<ResponseEntity<UserInfoResponseDto>> {
        return userService.getInfo(Mono.just(user))
    }

    @PostMapping("/update-token")
    fun updateAccessToken(@RequestBody @Valid dto: Mono<AccessTokenUpdateRequestDto>) = userService.updateAccessToken(dto)
}
