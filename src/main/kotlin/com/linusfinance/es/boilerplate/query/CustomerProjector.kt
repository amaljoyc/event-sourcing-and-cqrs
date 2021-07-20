package com.linusfinance.es.boilerplate.query

import com.linusfinance.es.boilerplate.coreapi.*
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.ResetHandler
import org.axonframework.eventhandling.SequenceNumber
import org.axonframework.messaging.Message
import org.axonframework.messaging.interceptors.MessageHandlerInterceptor
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component


@Component
class CustomerProjector(private val customerViewRepository: CustomerViewRepository) {

    @MessageHandlerInterceptor
    fun intercept(message: Message<*>) {
        println("on QUERY side -> $message")
    }

    /*
        use @SequenceNumber to save versions on Entity in order to handle eventual consistency issues
     */
    @EventHandler
    fun on(event: CustomerCreatedEvent, @SequenceNumber version: Long) {
        customerViewRepository.save(CustomerView(event.customerId, event.name, event.street, event.city, version))
    }

    @EventHandler
    fun on(event: CustomerActivatedEvent) {
        println("received CustomerActivatedEvent on QUERY side!")
    }

    @EventHandler
    fun on(event: AddressChangedEvent, @SequenceNumber version: Long) {
        val customerViewOptional = customerViewRepository.findById(event.customerId)
        if (customerViewOptional.isPresent()) {
            val customerView = customerViewOptional.get()
            customerView.street = event.street
            customerView.city = event.city
            customerView.version = version
            customerViewRepository.save(customerView)
        }
    }

    @EventHandler
    fun on(event: CustomerDeletedEvent) {
        customerViewRepository.deleteById(event.customerId)
    }

    @QueryHandler
    fun handle(query: FindCustomerViewQuery): CustomerView? {
        return customerViewRepository.findById(query.customerId).orElse(null)
    }

    // useful for eg, when we do a replay from the beginning
    @ResetHandler
    fun reset() {
        println("deleting old projections before replay...")
        customerViewRepository.deleteAll()
    }
}