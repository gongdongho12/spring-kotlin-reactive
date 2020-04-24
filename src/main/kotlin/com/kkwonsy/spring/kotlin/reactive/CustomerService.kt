package com.kkwonsy.spring.kotlin.reactive

interface CustomerService {
    fun getCustomer(id: Int) : Customer?
    fun searchCustomers(nameFilter: String) : List<Customer>
}