package com.kkwonsy.spring.kotlin.reactive

import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

interface CustomerService {
    fun getCustomer(id: Int) : Mono<Customer> // mono를 return 한다는건 publisher를 등록한다는거
    fun searchCustomers(nameFilter: String) : Flux<Customer>

    fun createCustomer(customerMono: Mono<Customer>): Mono<Customer>
    // mono를 받는다는건 subscriber가 된다는거
}