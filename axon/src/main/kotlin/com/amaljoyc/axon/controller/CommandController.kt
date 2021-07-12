package com.amaljoyc.axon.controller

import com.amaljoyc.axon.coreapi.ChangeAddressCommand
import com.amaljoyc.axon.coreapi.CreateCustomerCommand
import org.axonframework.commandhandling.gateway.CommandGateway
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController
import java.util.*
import java.util.concurrent.CompletableFuture


@RestController
class CommandController(private val commandGateway: CommandGateway) {

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