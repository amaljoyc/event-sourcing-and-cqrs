package com.linusfinance.es.boilerplate.controller

import com.linusfinance.es.boilerplate.coreapi.FindAccountViewByCustomerIdQuery
import com.linusfinance.es.boilerplate.coreapi.FindCustomerViewQuery
import com.linusfinance.es.boilerplate.query.AccountView
import com.linusfinance.es.boilerplate.query.CustomerView
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import java.util.*
import java.util.concurrent.CompletableFuture

@RestController
class QueryController(private val queryGateway: QueryGateway) {

    @GetMapping("/customer/{customerId}")
    fun findCustomer(@PathVariable("customerId") customerId: String): CompletableFuture<CustomerView?> {
        return queryGateway.query(
            FindCustomerViewQuery(UUID.fromString(customerId)),
            ResponseTypes.instanceOf(CustomerView::class.java)
        )
    }

    // for simplicity we assume only one account is created/possible for a customer
    @GetMapping("/customer/{customerId}/account")
    fun findCustomerAccount(@PathVariable("customerId") customerId: String): CompletableFuture<AccountView?> {
        return queryGateway.query(
            FindAccountViewByCustomerIdQuery(UUID.fromString(customerId)),
            ResponseTypes.instanceOf(AccountView::class.java)
        )
    }
}