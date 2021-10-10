package com.template.user.service

import com.template.user.domain.User
import com.template.user.domain.UserRepository
import com.template.user.dto.UserCreateRequestDto
import com.template.user.dto.UserInfoResponseDto
import com.template.user.exception.UserEmailConflictException
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.net.URI

@Service
class UserService(
    private val userRepository: UserRepository,
) {

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
}
