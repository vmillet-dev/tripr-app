package com.adsearch.domain.model

/**
 * Value object representing search results
 */
data class SearchResult(
    val ads: List<Ad>,
    val totalCount: Int,
    val sources: List<String>
)
