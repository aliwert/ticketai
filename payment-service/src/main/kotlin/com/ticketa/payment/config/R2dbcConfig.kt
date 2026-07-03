package com.ticketa.payment.config

import org.springframework.context.annotation.Configuration
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories

@Configuration
@EnableR2dbcRepositories(basePackages = ["com.ticketa.payment.infrastructure.persistence"])
@EnableR2dbcAuditing
class R2dbcConfig
