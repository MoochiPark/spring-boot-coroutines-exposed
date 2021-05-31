package io.wisoft.springexposed.handler

import io.wisoft.springexposed.repository.CustomerRepository
import java.net.URI
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.ServerResponse.accepted
import org.springframework.web.reactive.function.server.ServerResponse.badRequest
import org.springframework.web.reactive.function.server.ServerResponse.created
import org.springframework.web.reactive.function.server.ServerResponse.noContent
import org.springframework.web.reactive.function.server.ServerResponse.notFound
import org.springframework.web.reactive.function.server.ServerResponse.ok
import org.springframework.web.reactive.function.server.awaitBody
import org.springframework.web.reactive.function.server.bodyAndAwait
import org.springframework.web.reactive.function.server.bodyValueAndAwait
import org.springframework.web.reactive.function.server.buildAndAwait

@Component
class CustomerHandler(private val customerRepository: CustomerRepository) {
    suspend fun getAll(request: ServerRequest): ServerResponse =
        ok()
            .contentType(APPLICATION_JSON)
            .bodyAndAwait(customerRepository.findAll())

    suspend fun getById(request: ServerRequest): ServerResponse {
        val id = request.getLongOrNull("id")
            ?: return badRequest().buildAndAwait()
        return customerRepository.findById(id)?.let {
            ok().contentType(APPLICATION_JSON).bodyValueAndAwait(it)
        } ?: notFound().buildAndAwait()
    }

    suspend fun register(request: ServerRequest): ServerResponse =
        request.awaitBody<CustomerRequest>().run {
            if (existsByEmail(email)) {
                badRequest().bodyValueAndAwait("Duplicated email.")
            } else {
                customerRepository.insert(this).run {
                    created(URI.create("/customers/${id}")).buildAndAwait()
                }
            }
        }

    suspend fun update(request: ServerRequest): ServerResponse {
        val id = request.getLongOrNull("id")
            ?: return badRequest().buildAndAwait()
        return request.awaitBody<CustomerRequest>().run {
            if (existsByEmail(email)) {
                badRequest().bodyValueAndAwait("Duplicated email.")
            } else {
                customerRepository.update(id, this)
                noContent().buildAndAwait()
            }
        }
    }

    suspend fun delete(request: ServerRequest): ServerResponse {
        val id = request.getLongOrNull("id")
            ?: return badRequest().buildAndAwait()
        customerRepository.delete(id)
        return accepted().buildAndAwait()
    }

    private suspend fun existsByEmail(email: String) =
        customerRepository.existsByEmail(email)

    private fun ServerRequest.getLongOrNull(name: String) =
        this.pathVariable(name).toLongOrNull()
}




