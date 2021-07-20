package com.linusfinance.es.boilerplate.external

import com.linusfinance.es.boilerplate.coreapi.CustomerCreatedEvent
import com.linusfinance.es.boilerplate.coreapi.EmailTriggerRequestedEvent
import org.axonframework.eventhandling.EventHandler
import org.axonframework.eventhandling.GenericEventMessage.asEventMessage
import org.axonframework.eventhandling.gateway.EventGateway
import org.axonframework.messaging.Message
import org.axonframework.messaging.interceptors.MessageHandlerInterceptor
import org.springframework.stereotype.Component

/*
    just a sample component to showcase a scenario that could cause side effect (eg, sending email)
        and we don't want to do replays of such events and hence they don't belong to any Aggregates
 */
@Component
class SideEffectEventsHandler(private val eventGateway: EventGateway) {

    @MessageHandlerInterceptor
    fun intercept(message: Message<*>) {
        println("on SIDE-EFFECT handler -> $message")
    }

    @EventHandler
    fun on(event: CustomerCreatedEvent) {
        eventGateway.publish(
            asEventMessage<EmailTriggerRequestedEvent>(
                EmailTriggerRequestedEvent(
                    event.customerId,
                    event.name
                )
            )
        )
    }

    /*
        we don't wanna handle EmailTriggerRequestedEvent inside Customer aggregate as that would retrigger email upon replay
     */
    @EventHandler
    fun on(event: EmailTriggerRequestedEvent) {
        // mock
        println("calling email service to trigger email for customer: ${event.customerId}, ${event.name}")
    }
}