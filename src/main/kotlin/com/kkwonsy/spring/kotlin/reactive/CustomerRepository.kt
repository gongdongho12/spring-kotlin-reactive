package com.kkwonsy.spring.kotlin.reactive

import org.springframework.data.mongodb.core.ReactiveMongoTemplate
import org.springframework.data.mongodb.core.find
import org.springframework.data.mongodb.core.findById
import org.springframework.data.mongodb.core.query.Criteria.where
import org.springframework.data.mongodb.core.query.Query
import org.springframework.data.mongodb.core.query.isEqualTo
import org.springframework.data.mongodb.core.remove
import org.springframework.stereotype.Repository
import reactor.core.Disposable
import reactor.core.publisher.Mono
import reactor.kotlin.core.publisher.toMono
import javax.annotation.PostConstruct

//interface CustomerRepository : ReactiveCrudRepository<Customer, Int>{
//}
@Repository
class CustomerRepository(private val template: ReactiveMongoTemplate) {
    companion object {
        val initialCustomers = listOf(
                Customer(1, "Kotlin")
                , Customer(2, "Java")
                , Customer(3, "Javascript", Customer.Telephone("+81", "1231092718"))
                , Customer(4, "Python", Customer.Telephone("+82", "1029301928")))
    }

    @PostConstruct
    fun initData() = initialCustomers.map(Customer::toMono).map(this::create).map(Mono<Customer>::subscribe)

    fun create(customer: Mono<Customer>): Mono<Customer> = template.save(customer)

    fun findById(id: Int) = template.findById<Customer>(id)

    fun deleteById(id: Int) = template.remove<Customer>(Query(where("_id").isEqualTo(id)))

    fun findCustomer(nameFilter: String)
            = template.find<Customer>(Query(where("name").regex(".*$nameFilter.*", "i")))

    fun initData2(): List<Disposable> {
        val toMono: List<Mono<Customer>> = initialCustomers.map { customer -> customer.toMono() }
        val resultOfCreates: List<Mono<Customer>> = toMono.map { this.create(it) }
        return resultOfCreates.map { mono: Mono<Customer> -> mono.subscribe() }
    }
}