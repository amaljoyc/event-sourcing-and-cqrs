package com.linusfinance.es.boilerplate.query

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class CustomerView(
    @Id val customerId: UUID,
    val name: String,
    var street: String,
    var city: String,
    var version: Long
)

interface CustomerViewRepository : JpaRepository<CustomerView, UUID>