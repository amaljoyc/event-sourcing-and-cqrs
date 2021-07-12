package com.amaljoyc.axon.coreapi

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