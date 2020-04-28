package com.kkwonsy.spring.kotlin.mydiscovery

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer

@SpringBootApplication
@EnableEurekaServer
class MydiscoveryApplication

fun main(args: Array<String>) {
    runApplication<MydiscoveryApplication>(*args)
}
