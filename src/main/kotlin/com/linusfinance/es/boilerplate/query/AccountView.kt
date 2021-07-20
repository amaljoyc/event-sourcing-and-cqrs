package com.linusfinance.es.boilerplate.query

import org.springframework.data.jpa.repository.JpaRepository
import java.util.*
import javax.persistence.Entity
import javax.persistence.Id

@Entity
data class AccountView(
    @Id val accountId: UUID,
    val customerId: UUID,
    var balance: Long
)

interface AccountViewRepository : JpaRepository<AccountView, UUID> {
    fun findByCustomerId(customerId: UUID): Optional<AccountView>
}