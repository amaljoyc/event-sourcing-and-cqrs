package com.linusfinance.es.boilerplate.controller

import com.linusfinance.es.boilerplate.command.AggregateReader
import com.linusfinance.es.boilerplate.command.Customer
import com.linusfinance.es.boilerplate.coreapi.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.messaging.responsetypes.ResponseTypes
import org.axonframework.queryhandling.QueryGateway
import org.springframework.web.bind.annotation.*
import java.util.*
import java.util.concurrent.CompletableFuture


@RestController
class CommandController(
    private val commandGateway: CommandGateway,
    private val aggregateReader: AggregateReader,
    private val queryGateway: QueryGateway // inorder to query the event sourced aggregate directly
) {

    @PostMapping("/customer")
    fun createCustomer(@RequestBody customer: CreateCustomer): CompletableFuture<UUID> {
        return commandGateway.send(CreateCustomerCommand(customer.name, customer.street, customer.city))
    }

    @PostMapping("/customer/{id}/address")
    fun changeCustomerAddress(
        @PathVariable("id") customerId: String,
        @RequestBody address: CustomerAddress
    ): CompletableFuture<UUID> {
        return commandGateway.send(ChangeAddressCommand(UUID.fromString(customerId), address.street, address.city))
    }

    @DeleteMapping("/customer/{customerId}")
    fun deleteCustomer(@PathVariable("customerId") customerId: String): CompletableFuture<UUID> {
        return commandGateway.send(RequestCustomerDeletionCommand(UUID.fromString(customerId)))
    }

    @PostMapping("/account/{accountId}")
    fun creditCustomerAccount(
        @PathVariable("accountId") accountId: String,
        @RequestBody credit: CreditCustomerAccount
    ): CompletableFuture<UUID> {
        return commandGateway.send(CreditAccountCommand(UUID.fromString(accountId), credit.amount))
    }

    @GetMapping("/customer/{id}/events")
    fun getCustomerEvents(@PathVariable("id") customerId: String) =
        aggregateReader.fetchAllEventsForCustomer(customerId)

    @GetMapping("/customer/{id}/current")
    fun getCurrentCustomerState(@PathVariable("id") customerId: String) =
        queryGateway.query(
            FindCustomerAggregateQuery(customerId),
            ResponseTypes.instanceOf(Customer::class.java)
        )
}

data class CreateCustomer(
    val name: String,
    val street: String,
    val city: String
)

data class CustomerAddress(
    val street: String,
    val city: String
)

data class CreditCustomerAccount(
    val amount: Long
)