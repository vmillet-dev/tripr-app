package com.adsearch.domain.event

/**
 * Interface for publishing domain events
 */
interface DomainEventPublisher {
    fun publish(event: DomainEvent)
}
