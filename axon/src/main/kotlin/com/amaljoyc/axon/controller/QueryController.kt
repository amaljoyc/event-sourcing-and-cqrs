package com.amaljoyc.axon.controller

import com.amaljoyc.axon.coreapi.FindCustomerQuery
import com.amaljoyc.axon.query.CustomerView
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
    fun findCustomer(@PathVariable("customerId") customerId: String): CompletableFuture<CustomerView> {
        return queryGateway.query<CustomerView, FindCustomerQuery>(
            FindCustomerQuery(UUID.fromString(customerId)),
            ResponseTypes.instanceOf<CustomerView>(CustomerView::class.java)
        )
    }
}