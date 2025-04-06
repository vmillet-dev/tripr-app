package com.adsearch.infrastructure.adapter.`in`.web.dto

import com.adsearch.domain.model.Ad
import java.time.LocalDateTime

/**
 * Data Transfer Object for Ad entity
 */
data class AdDto(
    val id: Long,
    val title: String,
    val description: String,
    val price: Double?,
    val currency: String?,
    val imageUrl: String?,
    val sourceId: String,
    val sourceName: String,
    val externalUrl: String?,
    val createdAt: LocalDateTime,
    val category: String?,
    val location: String?,
    val tags: List<String>
) {
    companion object {
        /**
         * Convert domain model to DTO
         */
        fun fromDomain(ad: Ad): AdDto {
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
    }
}
