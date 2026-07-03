package com.ticketa.inventory.persistence

import org.springframework.data.repository.kotlin.CoroutineCrudRepository
import java.util.UUID

interface AuditoriumRepository : CoroutineCrudRepository<AuditoriumEntity, UUID>
