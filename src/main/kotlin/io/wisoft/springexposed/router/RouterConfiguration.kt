package io.wisoft.springexposed.router

import io.wisoft.springexposed.handler.CustomerHandler
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.MediaType.APPLICATION_JSON
import org.springframework.web.reactive.function.server.coRouter

@Configuration
class RouterConfiguration {
    @Bean
    fun customerRoutes(customerHandler: CustomerHandler) =
        coRouter {
            "/customers".nest {
                accept(APPLICATION_JSON).nest {
                    GET("", customerHandler::getAll)
                    POST("", customerHandler::register)
                    GET("{id}", customerHandler::getById)
                    PUT("{id}", customerHandler::update)
                    DELETE("{id}", customerHandler::delete)
                }
            }
        }
}

