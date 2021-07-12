package com.amaljoyc.axon.query

import com.amaljoyc.axon.coreapi.AddressChangedEvent
import com.amaljoyc.axon.coreapi.CustomerCreatedEvent
import com.amaljoyc.axon.coreapi.FindCustomerQuery
import org.axonframework.eventhandling.EventHandler
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component

@Component
class CustomerProjector(private val customerViewRepository: CustomerViewRepository) {

    @EventHandler
    fun on(event: CustomerCreatedEvent) {
        customerViewRepository.save(CustomerView(event.customerId, event.name, event.street, event.city))
        println("query -> " + event)
    }

    @EventHandler
    fun on(event: AddressChangedEvent) {
        val customerViewOptional = customerViewRepository.findById(event.customerId)
        if (customerViewOptional.isPresent()) {
            val customerView = customerViewOptional.get()
            customerView.street = event.street
            customerView.city = event.city
            customerViewRepository.save(customerView)
        }
        println("query -> " + event)
    }

    @QueryHandler
    fun handle(query: FindCustomerQuery): CustomerView {
        println(query)
        return customerViewRepository.findById(query.customerId).orElse(null)
    }
}