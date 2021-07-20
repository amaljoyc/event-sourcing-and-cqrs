package com.linusfinance.es.boilerplate.saga

import com.linusfinance.es.boilerplate.coreapi.*
import org.axonframework.commandhandling.gateway.CommandGateway
import org.axonframework.modelling.saga.EndSaga
import org.axonframework.modelling.saga.SagaEventHandler
import org.axonframework.modelling.saga.SagaLifecycle
import org.axonframework.modelling.saga.StartSaga
import org.axonframework.spring.stereotype.Saga
import org.springframework.beans.factory.annotation.Autowired

/*
    AxonFramework will automatically remove a Saga entry from its storage,
    including any associations, when it has ended.
    So you'll only ever see information of active instances on the SAGA_ENTRY table.
 */
@Saga
class CustomerDeletionSaga {

    @Autowired
    @Transient
    private lateinit var commandGateway: CommandGateway

    /*
        Sagas can have its own fields persisted, in order to use them throughout the saga journey
        I am not using this per say here, but just logging the different states of the Saga as of now
        but feel free to persists any fields here that could help in the saga process
     */
    private lateinit var status: DeletionSagaStatus

    @StartSaga
    @SagaEventHandler(associationProperty = "customerId")
    fun on(event: CustomerDeleteRequestedEvent) {
        this.status = DeletionSagaStatus.CUST_DELETE_REQUESTED
        println("Starting CustomerDeletionSaga with status ${this.status}")

        SagaLifecycle.associateWith("accountId", event.accountId.toString())
        commandGateway.send<CloseAccountCommand>(CloseAccountCommand(event.accountId))
    }

    @SagaEventHandler(associationProperty = "accountId")
    fun on(event: AccountClosedEvent) {
        this.status = DeletionSagaStatus.ACC_CLOSED
        println("Continuing CustomerDeletionSaga with status ${this.status}")
        commandGateway.send<DeleteCustomerCommand>(DeleteCustomerCommand(event.customerId))
    }

    @SagaEventHandler(associationProperty = "accountId")
    fun on(event: AccountClosingDeniedEvent) {
        this.status = DeletionSagaStatus.ACC_CLOSE_DENIED
        println("Continuing CustomerDeletionSaga with status ${this.status}")
        commandGateway.send<DenyCustomerDeletionCommand>(
            DenyCustomerDeletionCommand(
                event.customerId,
                "Cannot delete customer as the account cannot be closed!"
            )
        )
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "customerId")
    fun on(event: CustomerDeletedEvent) {
        this.status = DeletionSagaStatus.CUST_DELETED
        println("Ending CustomerDeletionSaga with status ${this.status}")
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "customerId")
    fun on(event: CustomerDeletionDeniedEvent) {
        this.status = DeletionSagaStatus.CUST_DELETE_DENIED
        println("Ending CustomerDeletionSaga with status ${this.status}")
    }
}

enum class DeletionSagaStatus {
    CUST_DELETE_REQUESTED,
    ACC_CLOSED,
    ACC_CLOSE_DENIED,
    CUST_DELETED,
    CUST_DELETE_DENIED
}