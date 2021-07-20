package com.linusfinance.es.boilerplate.coreapi

import java.util.*

data class FindCustomerViewQuery(val customerId: UUID) // for querying from Projection

data class FindCustomerAggregateQuery(val customerId: String) // for querying from aggregate

data class FindAccountViewByIdQuery(val accountId: UUID)
data class FindAccountViewByCustomerIdQuery(val customerId: UUID)