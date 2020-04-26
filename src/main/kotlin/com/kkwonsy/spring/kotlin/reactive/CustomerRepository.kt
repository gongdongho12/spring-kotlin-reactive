package com.kkwonsy.spring.kotlin.reactive

import org.springframework.data.repository.reactive.ReactiveCrudRepository

interface CustomerRepository : ReactiveCrudRepository<Customer, Int>{
}