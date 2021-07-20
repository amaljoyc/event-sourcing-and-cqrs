package com.linusfinance.es.boilerplate.query

import com.linusfinance.es.boilerplate.coreapi.*
import org.axonframework.eventhandling.EventHandler
import org.axonframework.messaging.Message
import org.axonframework.messaging.interceptors.MessageHandlerInterceptor
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component

@Component
class AccountProjector(private val accountViewRepository: AccountViewRepository) {

    @MessageHandlerInterceptor
    fun intercept(message: Message<*>) {
        println("on QUERY side -> $message")
    }

    @EventHandler
    fun on(event: AccountCreatedEvent) {
        accountViewRepository.save(AccountView(event.accountId, event.customerId, 0))
    }

    @EventHandler
    fun on(event: AccountCreditedEvent) {
        val accountViewOptional = accountViewRepository.findById(event.accountId)
        if (accountViewOptional.isPresent()) {
            val accountView = accountViewOptional.get()
            accountView.balance += event.amount
            accountViewRepository.save(accountView)
        }
    }

    @QueryHandler
    fun handle(query: FindAccountViewByIdQuery): AccountView? {
        return accountViewRepository.findById(query.accountId).orElse(null)
    }

    @QueryHandler
    fun handle(query: FindAccountViewByCustomerIdQuery): AccountView? {
        return accountViewRepository.findByCustomerId(query.customerId).orElse(null)
    }
}