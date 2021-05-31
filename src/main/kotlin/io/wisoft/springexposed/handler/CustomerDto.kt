package io.wisoft.springexposed.handler

import io.wisoft.springexposed.domain.Customer


data class CustomerRequest(
    val firstName: String,
    val lastName: String,
    val email: String
)

data class CustomerResponse(
    val id: Long,
    val firstName: String,
    val lastName: String,
    val email: String
) {
    companion object {
        fun of(customer: Customer) =
            CustomerResponse(
                id = customer.id.value,
                firstName = customer.firstName,
                lastName = customer.lastName,
                email = customer.email
            )
    }
}
