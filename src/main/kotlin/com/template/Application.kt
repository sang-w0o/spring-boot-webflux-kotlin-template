package com.template

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import reactor.blockhound.BlockHound
import java.io.FileInputStream

@SpringBootApplication
class Application {
    @Bean
    fun passwordEncoder(): BCryptPasswordEncoder {
        return BCryptPasswordEncoder(10)
    }
}

fun main(args: Array<String>) {
//    BlockHound.builder()
//        .allowBlockingCallsInside(FileInputStream::class.java.canonicalName, "readBytes")
//        .install()

//    BlockHound.install()
    BlockHound.builder()
        .allowBlockingCallsInside("java.io.FileInputStream", "readBytes")
//        .install()
    runApplication<Application>(*args)
}
