package com.adsearch.infrastructure.adapter.`in`.web.mapper

/**
 * Generic interface for DTO mappers
 * Follows hexagonal architecture principles by centralizing mapping between domain and infrastructure
 */
interface DtoMapper<DTO, D> {
    /**
     * Convert a domain model to a DTO
     */
    fun toDto(domainModel: D): DTO
    
    /**
     * Convert a DTO to a domain model
     */
    fun toDomain(dto: DTO): D
    
    /**
     * Convert a list of domain models to DTOs
     */
    fun toDtoList(domainModels: List<D>): List<DTO> {
        return domainModels.map { toDto(it) }
    }
    
    /**
     * Convert a list of DTOs to domain models
     */
    fun toDomainList(dtos: List<DTO>): List<D> {
        return dtos.map { toDomain(it) }
    }
}
