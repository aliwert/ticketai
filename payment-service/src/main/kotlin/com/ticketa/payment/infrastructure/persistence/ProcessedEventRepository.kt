package com.ticketa.payment.infrastructure.persistence

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import org.springframework.stereotype.Repository

@Repository
interface ProcessedEventRepository : CoroutineCrudRepository<ProcessedEventEntity, String>
