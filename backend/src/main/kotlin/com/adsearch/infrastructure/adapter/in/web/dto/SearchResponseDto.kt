package com.adsearch.infrastructure.adapter.`in`.web.dto

import com.adsearch.domain.model.SearchResult

/**
 * Data Transfer Object for search response
 */
data class SearchResponseDto(
    val ads: List<AdDto>,
    val totalCount: Int,
    val sources: List<String>,
    val page: Int,
    val pageSize: Int,
    val totalPages: Int
) {
    companion object {
        /**
         * Convert domain model to DTO
         */
        fun fromDomain(result: SearchResult, limit: Int, offset: Int): SearchResponseDto {
            val page = if (limit > 0) offset / limit + 1 else 1
            val totalPages = if (limit > 0) (result.totalCount + limit - 1) / limit else 1

            return SearchResponseDto(
                ads = result.ads.map { AdDto.fromDomain(it) },
                totalCount = result.totalCount,
                sources = result.sources,
                page = page,
                pageSize = limit,
                totalPages = totalPages
            )
        }
    }
}
