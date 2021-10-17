package com.template.user.domain

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository : ReactiveCrudRepository<User, String> {
    fun existsByEmail(email: String): Mono<Boolean>
    fun findByEmailAndPassword(email: String, password: String): Mono<User>
}
