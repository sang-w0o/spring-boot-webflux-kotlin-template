package com.template.user.domain

import org.springframework.data.repository.reactive.ReactiveCrudRepository
import org.springframework.stereotype.Repository
import reactor.core.publisher.Mono

@Repository
interface UserRepository : ReactiveCrudRepository<User, Int> {
    fun findByEmail(email: String): Mono<User>?
    fun existsByEmail(email: String): Mono<Boolean>
}
