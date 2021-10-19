package com.template.user.service

import com.template.security.exception.AuthenticateException
import com.template.security.tools.JwtTokenUtil
import com.template.user.domain.User
import com.template.user.domain.UserRepository
import com.template.user.dto.*
import com.template.user.exception.UserEmailConflictException
import com.template.user.exception.UserLoginException
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import reactor.core.publisher.Mono
import java.net.URI

@Service
class UserService(
    private val userRepository: UserRepository,
    private val jwtTokenUtil: JwtTokenUtil
) {

    @Transactional
    fun create(requestDto: Mono<UserCreateRequestDto>): Mono<ResponseEntity<UserInfoResponseDto>> {
        return requestDto
            .flatMap {
                userRepository.existsByEmail(it.email)
                    .filter { result -> result == false }
                    .switchIfEmpty(Mono.error(UserEmailConflictException("Duplicate email(${it.email})")))
                    .thenReturn(it)
            }
            .flatMap {
                userRepository.save(User(it.name, it.email, it.password))
            }
            .map {
                UserInfoResponseDto(it)
            }
            .map {
                ResponseEntity.created(URI.create("/v1/user")).body(it)
            }
    }

    @Transactional(readOnly = true)
    fun login(requestDto: Mono<UserLoginRequestDto>): Mono<ResponseEntity<UserLoginResponseDto>> {
        return requestDto
            .flatMap {
                userRepository.findByEmailAndPassword(it.email, it.password)
                    .switchIfEmpty(Mono.error(UserLoginException()))
                    .single()
            }
            .map {
                UserLoginResponseDto(jwtTokenUtil.generateAccessToken(it.id!!), jwtTokenUtil.generateAccessToken(it.id!!))
            }
            .map {
                ResponseEntity.ok().body(it)
            }
    }

    @Transactional(readOnly = true)
    fun getInfo(user: Mono<User>): Mono<ResponseEntity<UserInfoResponseDto>> {
        return user
            .map {
                UserInfoResponseDto(it)
            }
            .map {
                ResponseEntity.ok().body(it)
            }
    }

    @Transactional(readOnly = true)
    fun updateAccessToken(dto: Mono<AccessTokenUpdateRequestDto>): Mono<ResponseEntity<AccessTokenUpdateResponseDto>> {
        return dto
            .filter { !jwtTokenUtil.isTokenExpired(it.refreshToken) }
            .switchIfEmpty(Mono.error(AuthenticateException("RefreshToken has been expired.")))
            .map {
                val userId = jwtTokenUtil.extractUserId(it.refreshToken)
                val accessToken = jwtTokenUtil.generateRefreshToken(userId)
                val responseDto = AccessTokenUpdateResponseDto(accessToken)
                ResponseEntity.ok().body(responseDto)
            }
    }
}
