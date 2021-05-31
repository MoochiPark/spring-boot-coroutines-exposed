package io.wisoft.springexposed

import io.wisoft.springexposed.domain.Customers
import io.wisoft.springexposed.handler.CustomerRequest
import io.wisoft.springexposed.handler.CustomerResponse
import org.jetbrains.exposed.sql.SchemaUtils
import org.jetbrains.exposed.sql.batchInsert
import org.jetbrains.exposed.sql.insertAndGetId
import org.jetbrains.exposed.sql.transactions.transaction
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpHeaders.CONTENT_TYPE
import org.springframework.http.MediaType.APPLICATION_JSON_VALUE
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.expectBody
import org.springframework.test.web.reactive.server.expectBodyList

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class CustomerRoutesTests(@Autowired val client: WebTestClient) {

    val customers = listOf(
        CustomerRequest("Daewon", "Park", "moochi@kakao.com"),
        CustomerRequest("Sangmin", "Lee", "sangmin@kakao.com"),
        CustomerRequest("Soonho", "Hwang", "soonho@kakao.com"),
    )

    @AfterEach
    private fun initSchema(): Unit = transaction {
        SchemaUtils.apply {
            drop(Customers)
            create(Customers)
        }
    }

    @Test
    fun `새로운 고객 등록`() {
        client
            .post()
            .uri("/customers")
            .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .bodyValue(customers[0])
            .exchange()
            .expectStatus().isCreated
    }

    @Test
    fun `모든 고객 조회`() {
        insertThreeCustomers()
        client
            .get()
            .uri("/customers")
            .exchange()
            .expectStatus().isOk
            .expectHeader().valueEquals(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .expectBodyList<CustomerResponse>()
            .hasSize(3)
    }

    @Test
    fun `아이디로 고객 조회`() {
        val id = insertCustomerAndGetId()
        client
            .get()
            .uri("/customers/${id.value}")
            .exchange()
            .expectStatus().isOk
            .expectHeader().valueEquals(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .expectBody<CustomerResponse>()
    }

    @Test
    fun `없는 고객 조회`() {
        client
            .get()
            .uri("/customers/9999")
            .exchange()
            .expectStatus().isNotFound
            .expectBody().isEmpty
    }

    @Test
    fun `아이디로 고객 수정`() {
        val id = insertCustomerAndGetId()
        client
            .put()
            .uri("/customers/${id.value}")
            .header(CONTENT_TYPE, APPLICATION_JSON_VALUE)
            .bodyValue(customers[1])
            .exchange()
            .expectStatus().isNoContent
    }

    @Test
    fun `아이디로 고객 삭제`() {
        val id = insertCustomerAndGetId()
        client
            .delete()
            .uri("/customers/${id.value}")
            .exchange()
            .expectStatus().isAccepted
    }

    private fun insertThreeCustomers() = transaction {
        Customers.batchInsert(customers) { customer ->
            this[Customers.firstName] = customer.firstName
            this[Customers.lastName] = customer.lastName
            this[Customers.email] = customer.email
        }
    }

    private fun insertCustomerAndGetId() = transaction {
        Customers.insertAndGetId {
            it[firstName] = "Daewon"
            it[lastName] = "Park"
            it[email] = "moochi@kakao.com"
        }
    }

}
