package com.linusfinance.es.boilerplate.coreapi.incoming

// Incoming Events - are the events triggered by other domains and we listen to it (maybe we need a better naming for this)
data class ChangeAddressIncomingEvent(
    val customerId: String,
    val street: String,
    val city: String
)