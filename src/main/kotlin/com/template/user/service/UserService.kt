package com.template.user.service

import com.template.user.domain.User
import com.template.user.domain.UserRepository
import com.template.user.dto.UserCreateRequestDto
import com.template.user.dto.UserInfoResponseDto
import com.template.user.exception.UserEmailConflictException
import org.springframework.http.ResponseEntity
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.net.URI

@Service
class UserService(
    private val userRepository: UserRepository,
    private val passwordEncoder: BCryptPasswordEncoder
) {

    fun create(requestDto: Mono<UserCreateRequestDto>): Mono<ResponseEntity<UserInfoResponseDto>> {
        return requestDto
            .flatMap { dto ->
                userRepository.existsByEmail(dto.email)
                    .filter { result -> result == false }
                    .switchIfEmpty(Mono.error(UserEmailConflictException("Duplicate email(${dto.email})")))
                    .thenReturn(dto)
            }
            .flatMap {
                result ->
                userRepository.save(User(result.name, result.email, passwordEncoder.encode(result.password)))
            }
            .map {
                ResponseEntity.created(URI.create("/v1/user")).body(UserInfoResponseDto(it))
            }
    }
}
