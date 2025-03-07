package com.adsearch.domain.model

/**
 * Value object representing search criteria for ads
 */
data class SearchCriteria(
    val query: String? = null,
    val category: String? = null,
    val minPrice: Double? = null,
    val maxPrice: Double? = null,
    val location: String? = null,
    val sortBy: SortOption = SortOption.RELEVANCE,
    val limit: Int = 20,
    val offset: Int = 0
)

enum class SortOption {
    RELEVANCE,
    PRICE_ASC,
    PRICE_DESC,
    DATE_DESC,
    DATE_ASC
}
