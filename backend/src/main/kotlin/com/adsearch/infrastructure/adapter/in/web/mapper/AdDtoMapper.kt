package com.adsearch.infrastructure.adapter.`in`.web.mapper

import com.adsearch.common.mapper.mapTo
import com.adsearch.common.mapper.mapToList
import com.adsearch.domain.model.Ad
import com.adsearch.infrastructure.adapter.`in`.web.dto.AdDto
import org.springframework.stereotype.Component

/**
 * Mapper for converting between Ad domain model and AdDto
 */
@Component
class AdDtoMapper {
    
    fun toDto(domainModel: Ad): AdDto {
        return domainModel.mapTo<AdDto>()
    }
    
    fun toDomain(dto: AdDto): Ad {
        return dto.mapTo<Ad>()
    }
    
    fun toDtoList(domainModels: List<Ad>): List<AdDto> {
        return domainModels.mapToList<AdDto>()
    }
    
    fun toDomainList(dtos: List<AdDto>): List<Ad> {
        return dtos.mapToList<Ad>()
    }
}
