package com.adsearch.infrastructure.event

import com.adsearch.domain.event.DomainEvent
import com.adsearch.domain.event.DomainEventPublisher
import org.slf4j.LoggerFactory
import org.springframework.context.ApplicationEventPublisher
import org.springframework.stereotype.Component

/**
 * Spring implementation of the DomainEventPublisher
 */
@Component
class SpringDomainEventPublisher(
    private val applicationEventPublisher: ApplicationEventPublisher
) : DomainEventPublisher {
    
    private val logger = LoggerFactory.getLogger(SpringDomainEventPublisher::class.java)
    
    override fun publish(event: DomainEvent) {
        logger.debug("Publishing domain event: {}", event)
        applicationEventPublisher.publishEvent(event)
    }
}
