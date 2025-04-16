package com.adsearch.domain.event

import java.time.Instant

/**
 * Base interface for all domain events
 */
interface DomainEvent {
    val timestamp: Instant
    val type: String
}
