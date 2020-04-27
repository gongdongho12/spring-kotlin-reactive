package com.kkwonsy.spring.kotlin.reactive

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.BodyInserters.fromValue
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse.*
import org.springframework.web.reactive.function.server.bodyToMono
import reactor.kotlin.core.publisher.onErrorResume
import java.net.URI

@Component
class CustomerHandler(private val customerService: CustomerService) {

    fun get(serverRequest: ServerRequest) =
            customerService.getCustomer(serverRequest.pathVariable("id").toInt())
                    .flatMap { ok().body(fromValue(it))}
                    .switchIfEmpty(status(HttpStatus.NOT_FOUND).build())

    fun create(serverRequest: ServerRequest) =
            customerService.createCustomer(serverRequest.bodyToMono())
//                    .flatMap { status(HttpStatus.CREATED).body(fromValue(it))}
                    .flatMap { created(URI.create("/functional/customer/${it.id}")).build()}
                    .onErrorResume(Exception::class) {
                        badRequest().body(fromValue(
                                ErrorResponse("error creating customer", it.message ?: "error")))
                    }
    fun delete(serverRequest: ServerRequest) =
            customerService.deleteCustomer(serverRequest.pathVariable("id").toInt())
                    .flatMap {
                        if (it) ok().build()
                        else status(HttpStatus.NOT_FOUND).build()
                    }


    fun search(serverRequest: ServerRequest) =
            ok().body(customerService.searchCustomers(serverRequest.queryParam("nameFilter").orElse(""))
                    , Customer::class.java)

}