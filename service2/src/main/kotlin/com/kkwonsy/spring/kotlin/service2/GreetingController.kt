package com.kkwonsy.spring.kotlin.service2

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class GreetingController {

    @GetMapping("/greetings")
    fun greetings() = "hello docker"
}