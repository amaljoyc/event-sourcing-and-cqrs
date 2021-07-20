package com.linusfinance.es.boilerplate.external

import com.linusfinance.es.boilerplate.coreapi.ChangeAddressCommand
import com.linusfinance.es.boilerplate.coreapi.incoming.ChangeAddressIncomingEvent
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.config.ProcessingGroup
import org.axonframework.eventhandling.EventHandler
import org.axonframework.messaging.Message
import org.axonframework.messaging.interceptors.MessageHandlerInterceptor
import org.springframework.stereotype.Component
import java.util.*

/*
    handles Integration Events coming from external domains and publishes internal Domain Commands
    also in Axon, Aggregates do not receive events from "outside",
    hence such incoming external events needs to be handled in a separate Component
 */
@Component
@ProcessingGroup("amqpEvents")
class IntegrationEventsHandler(private val commandGateway: CommandGateway) {

    @MessageHandlerInterceptor
    fun intercept(message: Message<*>) {
        println("on INCOMING-EVENTS handler -> $message")
    }

    /*
        IMP Note:- inorder for axon to map messages listened from external rabbit queue to this event listener,
        we need to provide the following mandatory headers along with the payload in the rabbit queue
            - axon-message-type=com.linusfinance.es.boilerplate.coreapi.incoming.ChangeAddressIncomingEvent // must be the exact FQN of the event in the handler
            - axon-message-id=fa60968c-6905-46b5-8afe-6da853a4c51a // can be a random uuid
            - axon-message-timestamp=2021-07-20T11:09:26.345Z // can be the current timestamp

        if the external incoming message was triggered by a service built with axon, it will automatically
        include these headers (just make sure the event `type` FQN is same as the event package name used on this side - if we share `coreapi` lib, it would be same)
        but if the external service is a custom one without using axon, we have to explicitly pass in these headers with proper values

        (the routing key used to send the message to the rabbit queue is irrelevant for this case and can be anything chosen by the external domain/service)
     */
    @EventHandler
    fun on(event: ChangeAddressIncomingEvent) {
        commandGateway.send<ChangeAddressCommand>(
            ChangeAddressCommand(
                UUID.fromString(event.customerId),
                event.street,
                event.city
            )
        )
    }
}