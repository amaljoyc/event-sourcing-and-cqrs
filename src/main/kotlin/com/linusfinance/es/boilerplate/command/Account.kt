package com.linusfinance.es.boilerplate.command

import com.linusfinance.es.boilerplate.coreapi.*
import org.axonframework.commandhandling.CommandHandler
import org.axonframework.eventsourcing.EventSourcingHandler
import org.axonframework.messaging.Message
import org.axonframework.messaging.interceptors.MessageHandlerInterceptor
import org.axonframework.modelling.command.AggregateIdentifier
import org.axonframework.modelling.command.AggregateLifecycle.apply
import org.axonframework.spring.stereotype.Aggregate
import java.util.*

@Aggregate
class Account {

    @AggregateIdentifier
    private lateinit var accountId: UUID
    private lateinit var customerId: UUID
    private lateinit var balance: AccountBalance

    @MessageHandlerInterceptor
    fun intercept(message: Message<*>) {
        println("on COMMAND side (Account) -> $message")
    }

    constructor()

    @CommandHandler
    constructor(command: CreateAccountCommand) {
        apply(AccountCreatedEvent(command.accountId, command.customerId))
    }

    @CommandHandler
    fun handle(command: CreditAccountCommand) {
        apply(AccountCreditedEvent(command.accountId, command.amount))
    }

    @CommandHandler
    fun handle(command: CloseAccountCommand) {
        if (this.balance.isRemaining()) {
            apply(
                AccountClosingDeniedEvent(
                    this.accountId,
                    "Cannot close account due to balance remaining!",
                    this.customerId
                )
            )
        } else {
            apply(AccountClosedEvent(this.accountId, this.customerId))
        }
    }

    // this works
    @EventSourcingHandler
    fun on(event: AccountCreatedEvent) {
        this.accountId = event.accountId
        this.customerId = event.customerId
        this.balance = AccountBalance(0)
    }

    @EventSourcingHandler
    fun on(event: AccountCreditedEvent) {
        this.balance = this.balance.credit(event.amount)
    }

    // this would never work, as events for aggregates can only be handled if they were applied from a `command`
    @EventSourcingHandler
    fun on(event: CustomerActivatedEvent) {
        println("NEVER received CustomerActivatedEvent on Account aggregate!!!!!")
    }
}

data class AccountBalance(
    val value: Long // keeping it simple and not considering decimals
) {
    fun credit(amount: Long) = AccountBalance(this.value + amount)
    fun isRemaining() = this.value > 0
}