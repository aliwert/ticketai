package com.ticketa.auth.config

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity
import org.springframework.security.config.web.server.ServerHttpSecurity
import org.springframework.security.web.server.SecurityWebFilterChain

@Configuration
@EnableWebFluxSecurity
class SecurityConfig {

    @Bean
    fun securityWebFilterChain(http: ServerHttpSecurity): SecurityWebFilterChain {
        return http
            .csrf { it.disable() }
            .authorizeExchange {
                it.pathMatchers(HttpMethod.POST, "/api/auth/register").permitAll()
                    .pathMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                    .pathMatchers(HttpMethod.POST, "/api/auth/refresh").permitAll()
                    .pathMatchers(HttpMethod.GET, "/api/auth/jwks").permitAll()
                    .pathMatchers("/actuator/**").permitAll()
                    .anyExchange().authenticated()
            }
            .oauth2ResourceServer { it.jwt { } }
            .build()
    }
}
