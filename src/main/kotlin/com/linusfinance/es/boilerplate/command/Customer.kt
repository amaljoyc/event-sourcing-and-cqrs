package com.linusfinance.es.boilerplate.command

import com.linusfinance.es.boilerplate.coreapi.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.messaging.Message
import org.axonframework.messaging.interceptors.MessageHandlerInterceptor
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.modelling.command.AggregateMember
import org.axonframework.spring.stereotype.Aggregate
import java.util.*

@Aggregate(snapshotTriggerDefinition = "customerSnapshotTrigger")
class Customer {

    @AggregateIdentifier
    private lateinit var customerId: UUID
    private lateinit var name: String
    private lateinit var customerStatus: CustomerStatus
    private var accountId: UUID? = null // for simplicity we only support single account per customer

    @AggregateMember
    private lateinit var address: Address
    private var addressChangeCount: Int = 0

    constructor() // mandatory to create an emtpy constructor, otherwise axon will complain

    @MessageHandlerInterceptor
    fun intercept(message: Message<*>) {
        println("on COMMAND side -> $message")
    }

    @CommandHandler
    constructor(command: CreateCustomerCommand) {
        val customerId = UUID.randomUUID()
        apply(CustomerCreatedEvent(customerId, command.name, command.street, command.city))
    }

    @CommandHandler
    fun handle(command: ChangeAddressCommand) {
        // @TODO: investigate if there is a better way to change an existing address, maybe by creating a command for Address entity?
        if (addressChangeCount < 7) {
            apply(AddressChangedEvent(command.customerId, command.street, command.city))
        }
    }

    @CommandHandler
    fun handle(command: UpdateCustomerAccountReferenceCommand) {
        apply(CustomerAccountRefUpdatedEvent(command.accountId))
    }

    @CommandHandler
    fun handle(command: RequestCustomerDeletionCommand) {
        if (this.accountId == null) {
            apply(CustomerDeletionDeniedEvent(this.customerId, "No account present for deletion"))
        } else {
            apply(CustomerDeleteRequestedEvent(this.customerId, this.accountId!!))
        }
    }

    @CommandHandler
    fun handle(command: DeleteCustomerCommand) {
        apply(CustomerDeletedEvent(this.customerId))
    }

    @CommandHandler
    fun handle(command: DenyCustomerDeletionCommand) {
        apply(CustomerDeletionDeniedEvent(this.customerId, command.reason))
    }

    @EventSourcingHandler
    fun on(event: CustomerDeleteRequestedEvent) {
        this.customerStatus = CustomerStatus.DELETE_REQUESTED
    }

    @EventSourcingHandler
    fun on(event: CustomerDeletionDeniedEvent) {
        this.customerStatus = CustomerStatus.ACTIVE
    }

    @EventSourcingHandler
    fun on(event: CustomerDeletedEvent) {
        this.customerStatus = CustomerStatus.DELETED
    }

    @EventSourcingHandler
    fun on(event: CustomerCreatedEvent) {
        this.customerId = event.customerId
        this.name = event.name
        this.customerStatus = CustomerStatus.INACTIVE
        this.address = Address(event.street, event.city)
        this.addressChangeCount = 0

        /*
         its alright & valid to create new events from other event handlers
         this event can later be listened by event handlers on this aggregate as well as all the projections
         but this event can never be listened by event handlers on another aggregate (eg, Account)
         so we have to use external handlers in order to create separate command -> event flow (eg, CustomerAccountCommsHandler)
        */
        apply(CustomerActivatedEvent(customerId, CustomerStatus.ACTIVE))
    }

    @EventSourcingHandler
    fun on(event: CustomerActivatedEvent) {
        this.customerStatus = event.status
    }

    @EventSourcingHandler
    fun on(event: AddressChangedEvent) {
        /*
            @TODO: this is not the correct approach as this will create new Address instances for each replay
                    we rather need a way to edit existing address entity
         */
        this.address = Address(event.street, event.city)
        addressChangeCount++
    }

    @EventSourcingHandler
    fun on(event: CustomerAccountRefUpdatedEvent) {
        this.accountId = event.accountId
    }
}

enum class CustomerStatus {
    ACTIVE, INACTIVE, DELETE_REQUESTED, DELETED
}