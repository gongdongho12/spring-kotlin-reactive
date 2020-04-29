package com.kkwonsy.spring.kotlin.service3

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicInteger

@RestController
class HelloController {
    private val id: String = java.util.UUID.randomUUID().toString()

    companion object{
        val total: AtomicInteger = AtomicInteger()
    }

    @GetMapping("/hello")
    fun hello() = "hello I am $id and I have been called ${total.incrementAndGet()} times"
}