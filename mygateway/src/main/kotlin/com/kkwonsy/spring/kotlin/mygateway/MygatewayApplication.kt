package com.kkwonsy.spring.kotlin.mygateway

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.zuul.EnableZuulProxy

@SpringBootApplication
@EnableZuulProxy
class MygatewayApplication

fun main(args: Array<String>) {
    runApplication<MygatewayApplication>(*args)
}
