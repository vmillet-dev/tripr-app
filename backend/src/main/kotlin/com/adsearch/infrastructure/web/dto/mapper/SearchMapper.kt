package com.adsearch.infrastructure.web.dto.mapper

import com.adsearch.domain.model.Ad
import com.adsearch.domain.model.SearchCriteria
import com.adsearch.domain.model.SearchResult
import com.adsearch.domain.model.SortOption
import com.adsearch.infrastructure.web.dto.AdDto
import com.adsearch.infrastructure.web.dto.SearchRequestDto
import com.adsearch.infrastructure.web.dto.SearchResponseDto

/**
 * Utility class for mapping between search-related DTOs and domain models
 */
object SearchMapper {
    
    /**
     * Convert SearchRequestDto to SearchCriteria domain model
     */
    fun toDomain(dto: SearchRequestDto): SearchCriteria {
        return SearchCriteria(
            query = dto.query,
            category = dto.category,
            minPrice = dto.minPrice,
            maxPrice = dto.maxPrice,
            location = dto.location,
            sortBy = parseSortOption(dto.sortBy),
            limit = dto.limit,
            offset = dto.offset
        )
    }
    
    /**
     * Convert SearchResult domain model to SearchResponseDto
     */
    fun toDto(result: SearchResult, limit: Int, offset: Int): SearchResponseDto {
        val page = if (limit > 0) offset / limit + 1 else 1
        val totalPages = if (limit > 0) (result.totalCount + limit - 1) / limit else 1
        
        return SearchResponseDto(
            ads = result.ads.map { toDto(it) },
            totalCount = result.totalCount,
            sources = result.sources,
            page = page,
            totalPages = totalPages,
            pageSize = limit
        )
    }
    
    /**
     * Convert Ad domain model to AdDto
     */
    fun toDto(ad: Ad): AdDto {
        return AdDto(
            id = ad.id,
            title = ad.title,
            description = ad.description,
            price = ad.price,
            currency = ad.currency,
            imageUrl = ad.imageUrl,
            sourceId = ad.sourceId,
            sourceName = ad.sourceName,
            externalUrl = ad.externalUrl,
            createdAt = ad.createdAt,
            category = ad.category,
            location = ad.location,
            tags = ad.tags
        )
    }
    
    /**
     * Parse sort option from string
     */
    private fun parseSortOption(sortBy: String?): SortOption {
        return when (sortBy?.uppercase()) {
            "PRICE_ASC" -> SortOption.PRICE_ASC
            "PRICE_DESC" -> SortOption.PRICE_DESC
            "DATE_DESC" -> SortOption.DATE_DESC
            "DATE_ASC" -> SortOption.DATE_ASC
            else -> SortOption.RELEVANCE
        }
    }
}
