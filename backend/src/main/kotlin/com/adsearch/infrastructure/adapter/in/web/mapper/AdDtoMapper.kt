package com.adsearch.infrastructure.adapter.`in`.web.mapper

import com.adsearch.domain.model.Ad
import com.adsearch.infrastructure.adapter.`in`.web.dto.AdDto
import org.springframework.stereotype.Component

/**
 * Mapper for converting between Ad domain model and AdDto
 */
@Component
class AdDtoMapper : DtoMapper<AdDto, Ad> {
    
    override fun toDto(domainModel: Ad): AdDto {
        return AdDto(
            id = domainModel.id,
            title = domainModel.title,
            description = domainModel.description,
            price = domainModel.price,
            currency = domainModel.currency,
            imageUrl = domainModel.imageUrl,
            sourceId = domainModel.sourceId,
            sourceName = domainModel.sourceName,
            externalUrl = domainModel.externalUrl,
            createdAt = domainModel.createdAt,
            category = domainModel.category,
            location = domainModel.location,
            tags = domainModel.tags
        )
    }
    
    override fun toDomain(dto: AdDto): Ad {
        return Ad(
            id = dto.id,
            title = dto.title,
            description = dto.description,
            price = dto.price,
            currency = dto.currency,
            imageUrl = dto.imageUrl,
            sourceId = dto.sourceId,
            sourceName = dto.sourceName,
            externalUrl = dto.externalUrl,
            createdAt = dto.createdAt,
            category = dto.category,
            location = dto.location,
            tags = dto.tags
        )
    }
}
