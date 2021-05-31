package io.wisoft.springexposed.repository

import io.wisoft.springexposed.configure.query
import io.wisoft.springexposed.domain.Customer
import io.wisoft.springexposed.domain.Customers
import io.wisoft.springexposed.handler.CustomerRequest
import io.wisoft.springexposed.handler.CustomerResponse
import kotlinx.coroutines.flow.asFlow
import org.jetbrains.exposed.sql.SqlExpressionBuilder.eq
import org.jetbrains.exposed.sql.select
import org.springframework.stereotype.Component

@Component
class CustomerRepository {
    suspend fun findAll() = query {
        Customer
            .all()
            .map(CustomerResponse::of)
            .asFlow()
    }

    suspend fun findById(id: Long) = query {
        Customer
            .findById(id)
            ?.let { CustomerResponse.of(it) }
    }

    suspend fun existsByEmail(email: String) = query {
        Customers
            .select(Customers.email eq email)
            .any()
    }

    suspend fun insert(request: CustomerRequest) = query {
        Customer.new {
            email = request.email
            firstName = request.firstName
            lastName = request.lastName
        }
    }

    suspend fun update(id: Long, request: CustomerRequest) = query {
        Customer[id].apply {
            email = request.email
            firstName = request.firstName
            lastName = request.lastName
        }
    }

    suspend fun delete(id: Long) = query {
        Customer[id].delete()
    }
}
