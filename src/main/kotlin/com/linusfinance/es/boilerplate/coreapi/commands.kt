package com.linusfinance.es.boilerplate.coreapi

import org.axonframework.modelling.command.TargetAggregateIdentifier
import java.util.*

data class CreateCustomerCommand(
    val name: String,
    val street: String,
    val city: String
)

data class ChangeAddressCommand(
    @TargetAggregateIdentifier val customerId: UUID,
    val street: String,
    val city: String
)

// this command uses the aggregate -> aggregate flow
data class CreateAccountCommand(
    @TargetAggregateIdentifier val accountId: UUID,
    val customerId: UUID
)

data class CreditAccountCommand(
    @TargetAggregateIdentifier val accountId: UUID,
    val amount: Long
)

data class UpdateCustomerAccountReferenceCommand(
    @TargetAggregateIdentifier val customerId: UUID,
    val accountId: UUID
)

data class RequestCustomerDeletionCommand(
    @TargetAggregateIdentifier val customerId: UUID
)

data class CloseAccountCommand(
    @TargetAggregateIdentifier val accountId: UUID
)

data class DeleteCustomerCommand(
    @TargetAggregateIdentifier val customerId: UUID
)

data class DenyCustomerDeletionCommand(
    @TargetAggregateIdentifier val customerId: UUID,
    val reason: String
)