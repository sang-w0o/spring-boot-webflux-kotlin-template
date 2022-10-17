package com.template.user.controller

import com.template.config.annotation.LoggedInUser
import com.template.user.controller.request.AccessTokenUpdateRequest
import com.template.user.controller.request.UserCreateRequest
import com.template.user.controller.request.UserLoginRequest
import com.template.user.controller.response.AccessTokenUpdateResponse
import com.template.user.controller.response.UserInfoResponse
import com.template.user.controller.response.UserLoginResponse
import com.template.user.domain.User
import com.template.user.service.UserService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Mono
import java.net.URI
import javax.validation.Valid

@RestController
@RequestMapping("/v1/user")
class UserApiController(
    private val userService: UserService
) {

    @PostMapping("/signup")
    fun createUser(@RequestBody @Valid request: Mono<UserCreateRequest>): Mono<ResponseEntity<UserInfoResponse>> {
        return userService.create(request.map { it.toDto() })
            .map { ResponseEntity.created(URI.create("/v1/user")).body(UserInfoResponse.from(it.body!!)) }
    }

    @PostMapping("/login")
    fun login(@RequestBody @Valid request: Mono<UserLoginRequest>): Mono<ResponseEntity<UserLoginResponse>> {
        return userService.login(request.map { it.toDto() })
            .map { ResponseEntity.ok().body(UserLoginResponse.from(it.body!!)) }
    }

    @GetMapping("/info")
    fun getUserInfo(@LoggedInUser user: User): Mono<ResponseEntity<UserInfoResponse>> {
        return userService.getInfo(Mono.just(user))
            .map { ResponseEntity.ok().body(UserInfoResponse.from(it.body!!)) }
    }

    @PostMapping("/update-token")
    fun updateAccessToken(@RequestBody @Valid request: Mono<AccessTokenUpdateRequest>): Mono<ResponseEntity<AccessTokenUpdateResponse>> {
        return userService.updateAccessToken(request.map { it.refreshToken })
            .map { ResponseEntity.ok().body(AccessTokenUpdateResponse.from(it.body!!)) }
    }
}
