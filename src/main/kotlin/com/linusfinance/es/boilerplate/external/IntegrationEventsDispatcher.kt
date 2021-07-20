package com.linusfinance.es.boilerplate.external

import com.linusfinance.es.boilerplate.coreapi.CustomerCreatedEvent
import com.linusfinance.es.boilerplate.coreapi.CustomerCreatedIntegrationEvent
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.GenericEventMessage
import org.axonframework.eventhandling.gateway.EventGateway
import org.axonframework.messaging.Message
import org.axonframework.messaging.interceptors.MessageHandlerInterceptor
import org.springframework.stereotype.Component

/*
    handles some of the internal Domain Events and publishes external Integration Events to other domains
 */
@Component
class IntegrationEventsDispatcher(private val eventGateway: EventGateway) {

    @MessageHandlerInterceptor
    fun intercept(message: Message<*>) {
        println("on INTEGRATION-EVENTS dispatcher -> $message")
    }

    @EventHandler
    fun on(event: CustomerCreatedEvent) {
        eventGateway.publish(
            GenericEventMessage.asEventMessage<CustomerCreatedIntegrationEvent>(
                CustomerCreatedIntegrationEvent(
                    event.customerId,
                    event.name
                )
            )
        )
    }
}