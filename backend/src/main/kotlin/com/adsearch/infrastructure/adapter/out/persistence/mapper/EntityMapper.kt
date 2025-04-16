package com.adsearch.infrastructure.adapter.out.persistence.mapper

/**
 * Generic entity mapper interface
 */
interface EntityMapper<E, D> {
    /**
     * Map entity to domain model
     */
    fun toDomain(entity: E): D

    /**
     * Map domain model to entity
     */
    fun fromDomain(domain: D): E
}
