package com.adsearch.infrastructure.adapter.`in`.web.dto

import com.adsearch.domain.enum.SortOptionEnum
import com.adsearch.domain.model.SearchCriteria
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.Size

/**
 * Data Transfer Object for search requests
 */
data class SearchRequestDto(
    @field:Size(min = 0, max = 100, message = "Query must be between 0 and 100 characters")
    val query: String? = null,

    val category: String? = null,

    @field:Min(value = 0, message = "Minimum price cannot be negative")
    val minPrice: Double? = null,

    @field:Min(value = 0, message = "Maximum price cannot be negative")
    val maxPrice: Double? = null,

    val location: String? = null,

    val sortBy: String? = null,

    @field:Min(value = 1, message = "Limit must be at least 1")
    @field:Max(value = 100, message = "Limit cannot exceed 100")
    val limit: Int = 20,

    @field:Min(value = 0, message = "Offset cannot be negative")
    val offset: Int = 0
) {
    /**
     * Convert DTO to domain model
     */
    fun toDomain(): SearchCriteria {
        return SearchCriteria(
            query = query,
            category = category,
            minPrice = minPrice,
            maxPrice = maxPrice,
            location = location,
            sortBy = parseSortOption(sortBy),
            limit = limit,
            offset = offset
        )
    }

    private fun parseSortOption(sortBy: String?): SortOptionEnum {
        return when (sortBy?.uppercase()) {
            "PRICE_ASC" -> SortOptionEnum.PRICE_ASC
            "PRICE_DESC" -> SortOptionEnum.PRICE_DESC
            "DATE_DESC" -> SortOptionEnum.DATE_DESC
            "DATE_ASC" -> SortOptionEnum.DATE_ASC
            else -> SortOptionEnum.RELEVANCE
        }
    }
}
