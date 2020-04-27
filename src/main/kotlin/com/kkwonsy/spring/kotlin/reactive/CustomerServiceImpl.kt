package com.kkwonsy.spring.kotlin.reactive

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toFlux
import reactor.kotlin.core.publisher.toMono
import java.util.concurrent.ConcurrentHashMap

@Component
class CustomerServiceImpl : CustomerService {

    @Autowired
    lateinit var customerRepository: CustomerRepository

    override fun getCustomer(id: Int) = customerRepository.findById(id)

    override fun searchCustomers(nameFilter: String): Flux<Customer> = customerRepository.findCustomer(nameFilter)

    override fun createCustomer(customerMono: Mono<Customer>): Mono<Customer>
            = customerRepository.create(customerMono)

    override fun deleteCustomer(id: Int): Mono<Boolean>
            = customerRepository.deleteById(id).map { it.deletedCount > 0 }

//    companion object {
//        val initialCustomers = arrayOf(
//                Customer(1, "Kotlin")
//                , Customer(2, "Java")
//                , Customer(3, "Javascript")
//                , Customer(4, "Python"))
//    }
//
//    val customers = ConcurrentHashMap(initialCustomers.associateBy(Customer::id))
//
//    override fun getCustomer(id: Int) = customers[id]?.toMono() ?: Mono.empty()
//
//    override fun searchCustomers(nameFilter: String): Flux<Customer> =
//            customers
//                    .filter { it.value.name.contains(nameFilter, true) }
//                    .map { it.value }.toFlux()
//
//    override fun createCustomer(customerMono: Mono<Customer>): Mono<Customer> {
//        return customerMono
//                .flatMap {
//                    if (customers[it.id] == null) {
//                        customers[it.id] = it
//                        it.toMono()
//                    } else {
//                        Mono.error(CustomerExistException("Customer ${it.id} already exist"))
//                    }
//                }
//    }
}