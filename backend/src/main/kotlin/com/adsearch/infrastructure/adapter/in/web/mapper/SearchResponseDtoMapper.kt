package com.adsearch.infrastructure.adapter.`in`.web.mapper

import com.adsearch.domain.model.SearchResult
import com.adsearch.infrastructure.adapter.`in`.web.dto.SearchResponseDto
import org.springframework.stereotype.Component

/**
 * Mapper for converting between SearchResult domain model and SearchResponseDto
 */
@Component
class SearchResponseDtoMapper(
    private val adDtoMapper: AdDtoMapper
) : DtoMapper<SearchResponseDto, SearchResult> {
    
    override fun toDto(domainModel: SearchResult): SearchResponseDto {
        // Default implementation without pagination info
        return toDto(domainModel, 20, 0)
    }
    
    fun toDto(domainModel: SearchResult, limit: Int, offset: Int): SearchResponseDto {
        val page = if (limit > 0) offset / limit + 1 else 1
        val totalPages = if (limit > 0) (domainModel.totalCount + limit - 1) / limit else 1
        
        return SearchResponseDto(
            ads = adDtoMapper.toDtoList(domainModel.ads),
            totalCount = domainModel.totalCount,
            sources = domainModel.sources,
            page = page,
            pageSize = limit,
            totalPages = totalPages
        )
    }
    
    override fun toDomain(dto: SearchResponseDto): SearchResult {
        // This is a one-way mapper primarily, but we'll implement a basic conversion
        return SearchResult(
            ads = adDtoMapper.toDomainList(dto.ads),
            totalCount = dto.totalCount,
            sources = dto.sources
        )
    }
}
