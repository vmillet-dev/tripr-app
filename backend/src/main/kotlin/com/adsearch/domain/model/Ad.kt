package com.adsearch.domain.model

import java.time.LocalDateTime

/**
 * Core domain entity representing an advertisement
 */
data class Ad(
    val id: Long = 0,
    val title: String,
    val description: String,
    val price: Double? = null,
    val currency: String? = null,
    val imageUrl: String? = null,
    val sourceId: String,
    val sourceName: String,
    val externalUrl: String? = null,
    val createdAt: LocalDateTime = LocalDateTime.now(),
    val category: String? = null,
    val location: String? = null,
    val tags: List<String> = emptyList()
)
