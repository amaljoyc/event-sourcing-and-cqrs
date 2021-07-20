package com.linusfinance.es.boilerplate.command

import com.linusfinance.es.boilerplate.coreapi.AccountCreatedEvent
import com.linusfinance.es.boilerplate.coreapi.CreateAccountCommand
import com.linusfinance.es.boilerplate.coreapi.CustomerActivatedEvent
import com.linusfinance.es.boilerplate.coreapi.UpdateCustomerAccountReferenceCommand
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.eventhandling.EventHandler
import org.axonframework.messaging.Message
import org.axonframework.messaging.interceptors.MessageHandlerInterceptor
import org.springframework.stereotype.Component
import java.util.*

/*
    a simple saga, ideally if you have more complex business process, it is better to go with the Saga provided by axon
    but for some simple usecases, we could also do with a simple service class like this.
 */
@Component
class CustomerAccountCommsHandler(
    private val commandGateway: CommandGateway
) {

    @MessageHandlerInterceptor
    fun intercept(message: Message<*>) {
        println("on CustomerAccountCommsHandler -> $message")
    }

    @EventHandler
    fun on(event: CustomerActivatedEvent) {
        commandGateway.send<CreateAccountCommand>(CreateAccountCommand(UUID.randomUUID(), event.customerId))
    }

    @EventHandler
    fun on(event: AccountCreatedEvent) {
        commandGateway.send<UpdateCustomerAccountReferenceCommand>(
            UpdateCustomerAccountReferenceCommand(
                event.customerId,
                event.accountId
            )
        )
    }
}