package com.adsearch.infrastructure.adapter.`in`.web.mapper

import com.adsearch.common.mapper.mapToWithCustomMappings
import com.adsearch.domain.model.SearchResult
import com.adsearch.infrastructure.adapter.`in`.web.dto.SearchResponseDto
import org.springframework.stereotype.Component

/**
 * Mapper for converting between SearchResult domain model and SearchResponseDto
 */
@Component
class SearchResponseDtoMapper(
    private val adDtoMapper: AdDtoMapper
) {
    
    fun toDto(domainModel: SearchResult): SearchResponseDto {
        // Default implementation without pagination info
        return toDto(domainModel, 20, 0)
    }
    
    fun toDto(domainModel: SearchResult, limit: Int, offset: Int): SearchResponseDto {
        val page = if (limit > 0) offset / limit + 1 else 1
        val totalPages = if (limit > 0) (domainModel.totalCount + limit - 1) / limit else 1
        
        return domainModel.mapToWithCustomMappings<SearchResult, SearchResponseDto>(
            mapOf(
                "ads" to { source: SearchResult -> adDtoMapper.toDtoList(source.ads) },
                "page" to { _: SearchResult -> page },
                "pageSize" to { _: SearchResult -> limit },
                "totalPages" to { _: SearchResult -> totalPages }
            )
        )
    }
    
    fun toDomain(dto: SearchResponseDto): SearchResult {
        return dto.mapToWithCustomMappings<SearchResponseDto, SearchResult>(
            mapOf(
                "ads" to { source: SearchResponseDto -> adDtoMapper.toDomainList(source.ads) }
            )
        )
    }
}
