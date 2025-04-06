package com.adsearch.application.service

import com.adsearch.application.usecase.AdSearchUseCase
import com.adsearch.domain.model.SearchCriteria
import com.adsearch.domain.model.SearchResult
import com.adsearch.domain.port.AdSearchPort
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.coroutineScope

/**
 * Implementation of the AdSearchUseCase that orchestrates searching across multiple ad sources
 */
@Service
class AdSearchService(
    private val adSearchPorts: List<AdSearchPort>
) : AdSearchUseCase {

    private val logger = LoggerFactory.getLogger(AdSearchService::class.java)

    override suspend fun searchAdsAcrossSources(criteria: SearchCriteria): SearchResult = coroutineScope {
        logger.info("Searching ads across all sources with criteria: $criteria")

        val availableSources = adSearchPorts.filter {
            try {
                it.isAvailable()
            } catch (e: Exception) {
                logger.warn("Error checking availability for source ${it.sourceName}", e)
                false
            }
        }

        if (availableSources.isEmpty()) {
            logger.warn("No available ad sources found")
            return@coroutineScope SearchResult(emptyList(), 0, emptyList())
        }

        val searchResults = availableSources.map { source ->
            async {
                try {
                    source.searchAds(criteria)
                } catch (e: Exception) {
                    logger.error("Error searching ads from source ${source.sourceName}", e)
                    throw RuntimeException()
                }
            }
        }.awaitAll()

        val allAds = searchResults.flatMap { it.ads }
        val totalCount = searchResults.sumOf { it.totalCount }
        val sources = searchResults.flatMap { it.sources }.distinct()

        logger.info("Found $totalCount ads from ${sources.size} sources")

        SearchResult(allAds, totalCount, sources)
    }

    override suspend fun getAvailableSources(): List<String> {
        logger.info("Getting available ad sources")

        return adSearchPorts.filter {
            try {
                it.isAvailable()
            } catch (e: Exception) {
                logger.warn("Error checking availability for source ${it.sourceName}", e)
                false
            }
        }.map { it.sourceName }
    }
}
