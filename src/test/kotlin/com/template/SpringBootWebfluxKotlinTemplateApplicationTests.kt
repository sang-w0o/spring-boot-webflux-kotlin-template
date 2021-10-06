package com.template

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles

@SpringBootTest
@ActiveProfiles("test")
class SpringBootWebfluxKotlinTemplateApplicationTests {

    @Test
    fun contextLoads() {
        val a = 1 + 3
        assertEquals(4, a)
    }
}
