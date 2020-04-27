package com.kkwonsy.spring.kotlin.reactive

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono

@RestController
@RequestMapping("/blocking")
class CustomerController {

    @Autowired
    private lateinit var customerService: CustomerService

    // 스프링은 Mono<Customer> 라는 publisher가 등록되었기 때문에
    // 이 publisher를 구독하게 된다.
    @GetMapping("/customer/{id}")
    fun getCustomer(@PathVariable id: Int): ResponseEntity<Mono<Customer>> {
        val customer = customerService.getCustomer(id)
        return ResponseEntity(customer, HttpStatus.OK)
    }

    @GetMapping("/customers")
    fun getCustomers(@RequestParam(required = false, defaultValue = "") nameFilter: String)
            : ResponseEntity<Flux<Customer>> {
        val customers = customerService.searchCustomers(nameFilter)
        return ResponseEntity(customers, HttpStatus.OK)
    }

    @PostMapping("/customer")
    fun createCustomer(@RequestBody customerMono: Mono<Customer>) =
            ResponseEntity(customerService.createCustomer(customerMono), HttpStatus.OK)

}