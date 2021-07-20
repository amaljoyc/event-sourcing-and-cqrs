package com.linusfinance.es.boilerplate.coreapi

import com.linusfinance.es.boilerplate.command.CustomerStatus
import java.util.*

data class CustomerCreatedEvent(
    val customerId: UUID,
    val name: String,
    val street: String,
    val city: String
)

data class CustomerActivatedEvent(
    val customerId: UUID,
    val status: CustomerStatus
)

data class AddressChangedEvent(
    val customerId: UUID,
    val street: String,
    val city: String
)

data class AccountCreatedEvent(
    val accountId: UUID,
    val customerId: UUID
)

data class EmailTriggerRequestedEvent(
    val customerId: UUID,
    val name: String
)

data class AccountCreditedEvent(
    val accountId: UUID,
    val amount: Long
)

data class CustomerAccountRefUpdatedEvent(
    val accountId: UUID
)

data class CustomerDeleteRequestedEvent(
    val customerId: UUID,
    val accountId: UUID
)

data class CustomerDeletionDeniedEvent(
    val customerId: UUID,
    val reason: String
)

data class CustomerDeletedEvent(
    val customerId: UUID
)

data class AccountClosedEvent(
    val accountId: UUID,
    val customerId: UUID
)

data class AccountClosingDeniedEvent(
    val accountId: UUID,
    val reason: String,
    val customerId: UUID
)


// Integration Events - are the events we trigger to the outside (so other domains can listen to it)

interface IntegrationEvent

data class CustomerCreatedIntegrationEvent(
    val customerId: UUID,
    val name: String
) : IntegrationEvent
