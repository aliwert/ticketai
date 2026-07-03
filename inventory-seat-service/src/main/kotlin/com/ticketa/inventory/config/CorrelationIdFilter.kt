package com.ticketa.inventory.config

import org.springframework.core.Ordered
import org.springframework.core.annotation.Order
import org.springframework.stereotype.Component
import org.springframework.web.server.ServerWebExchange
import org.springframework.web.server.WebFilter
import org.springframework.web.server.WebFilterChain
import reactor.core.publisher.Mono
import java.util.UUID

@Component
@Order(Ordered.HIGHEST_PRECEDENCE)
class CorrelationIdFilter : WebFilter {

    companion object {
        const val CORRELATION_ID_HEADER = "X-Correlation-Id"
        const val CORRELATION_ID_KEY = "correlation-id"
    }

    override fun filter(exchange: ServerWebExchange, chain: WebFilterChain): Mono<Void> {
        val correlationId = exchange.request.headers
            .getFirst(CORRELATION_ID_HEADER)
            ?: UUID.randomUUID().toString()

        exchange.response.headers.add(CORRELATION_ID_HEADER, correlationId)

        return chain.filter(exchange)
            .contextWrite { ctx -> ctx.put(CORRELATION_ID_KEY, correlationId) }
    }
}
