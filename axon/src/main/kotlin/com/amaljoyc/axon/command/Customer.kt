package com.amaljoyc.axon.command

import com.amaljoyc.axon.coreapi.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.modelling.command.AggregateMember
import org.axonframework.spring.stereotype.Aggregate
import java.util.*


@Aggregate
class Customer {

    @AggregateIdentifier
    private lateinit var customerId: UUID
    private lateinit var name: String

    @AggregateMember
    private lateinit var address: Address

    constructor() // mandatory to create an emtpy constructor, otherwise axon will complain

    @CommandHandler
    constructor(command: CreateCustomerCommand) {
        val customerId = UUID.randomUUID()
        apply(CustomerCreatedEvent(customerId, command.name, command.street, command.city))
    }

    @CommandHandler
    fun handle(command: ChangeAddressCommand) {
        // @TODO: investigate if there is a better way to change an existing address, maybe by creating a command for Address entity?
        apply(AddressChangedEvent(command.street, command.city))
    }

    @EventSourcingHandler
    fun on(event: CustomerCreatedEvent) {
        this.customerId = event.customerId
        this.name = event.name
        this.address = Address(event.street, event.city)
    }

    @EventSourcingHandler
    fun on(event: AddressChangedEvent) {
        this.address = Address(event.street, event.city)
    }
}