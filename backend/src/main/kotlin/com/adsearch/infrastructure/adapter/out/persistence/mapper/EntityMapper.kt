package com.adsearch.infrastructure.adapter.out.persistence.mapper

/**
 * Generic interface for entity mappers
 * Follows hexagonal architecture principles by centralizing mapping between domain and infrastructure
 */
interface EntityMapper<E, D> {
    /**
     * Convert a domain model to an entity
     */
    fun toEntity(domainModel: D): E
    
    /**
     * Convert an entity to a domain model
     */
    fun toDomain(entity: E): D
    
    /**
     * Convert a list of domain models to entities
     */
    fun toEntityList(domainModels: List<D>): List<E> {
        return domainModels.map { toEntity(it) }
    }
    
    /**
     * Convert a list of entities to domain models
     */
    fun toDomainList(entities: List<E>): List<D> {
        return entities.map { toDomain(it) }
    }
}
