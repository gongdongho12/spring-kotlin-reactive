package com.kkwonsy.spring.kotlin.reactive

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.mongodb.core.ReactiveMongoOperations
import org.springframework.stereotype.Component
import javax.annotation.PostConstruct

//@Component
class DatabaseInitializer {

//    @Autowired
//    lateinit var customerRepository: CustomerRepository

//    companion object {
//        val initialCustomers = listOf(
//                Customer(1, "Kotlin")
//                , Customer(2, "Java")
//                , Customer(3, "Javascript")
//                , Customer(4, "Python"))
//    }

//    @PostConstruct
    fun initData() {
//        customerRepository.saveAll(initialCustomers).subscribe {
//            println("Default customers created")
//        }
    }
}