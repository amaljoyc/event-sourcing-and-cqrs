package com.linusfinance.es.boilerplate.command

import com.linusfinance.es.boilerplate.coreapi.FindCustomerAggregateQuery
import org.axonframework.eventsourcing.EventSourcingRepository
import org.axonframework.eventsourcing.eventstore.EventStore
import org.axonframework.modelling.command.Repository
import org.axonframework.queryhandling.QueryHandler
import org.springframework.stereotype.Component
import java.util.stream.Collectors


@Component
class AggregateReader(
    private val eventStore: EventStore,
    private val customerRepository: Repository<Customer>
) {

    fun fetchAllEventsForCustomer(customerId: String): List<Any> {
        return eventStore.readEvents(customerId).asStream()
            .map { e -> e.payload }
            .collect(Collectors.toList())
    }

    @QueryHandler
    fun fetchCustomer(query: FindCustomerAggregateQuery): Customer {
        return (customerRepository as EventSourcingRepository)
            .load(query.customerId).wrappedAggregate.aggregateRoot
    }
}