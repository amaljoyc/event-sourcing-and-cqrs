package com.linusfinance.es.boilerplate.command

import org.axonframework.modelling.command.EntityId
import java.util.*

class Address {

    @EntityId
    private var addressId: UUID
    private var street: String
    private var city: String

    constructor(street: String, city: String) {
        this.addressId = UUID.randomUUID()
        this.street = street
        this.city = city
    }
}