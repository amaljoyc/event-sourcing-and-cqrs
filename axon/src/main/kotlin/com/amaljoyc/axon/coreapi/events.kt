package com.amaljoyc.axon.coreapi

import java.util.*

data class CustomerCreatedEvent(
    val customerId: UUID,
    val name: String,
    val street: String,
    val city: String
)

data class AddressChangedEvent(
    val customerId: UUID,
    val street: String,
    val city: String
)