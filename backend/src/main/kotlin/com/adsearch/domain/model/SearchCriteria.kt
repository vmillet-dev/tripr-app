package com.adsearch.domain.model

import com.adsearch.domain.enum.SortOptionEnum

/**
 * Value object representing search criteria for ads
 */
data class SearchCriteria(
    val query: String? = null,
    val category: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val location: String? = null,
    val sortBy: SortOptionEnum = SortOptionEnum.RELEVANCE,
    val limit: Int = 20,
    val offset: Int = 0
)

