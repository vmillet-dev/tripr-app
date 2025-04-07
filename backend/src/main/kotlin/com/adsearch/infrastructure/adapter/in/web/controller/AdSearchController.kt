package com.adsearch.infrastructure.adapter.`in`.web.controller

import com.adsearch.application.usecase.AdSearchUseCase
import com.adsearch.infrastructure.adapter.`in`.web.dto.SearchRequestDto
import com.adsearch.infrastructure.adapter.`in`.web.dto.SearchResponseDto
import com.adsearch.infrastructure.adapter.`in`.web.mapper.SearchRequestDtoMapper
import com.adsearch.infrastructure.adapter.`in`.web.mapper.SearchResponseDtoMapper
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.slf4j.LoggerFactory
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/**
 * REST controller for ad search operations
 */
@RestController
@RequestMapping("/ads")
@Tag(name = "Ad Search", description = "API for searching ads across multiple sources")
class AdSearchController(
    private val adSearchUseCase: AdSearchUseCase,
    private val ioDispatcher: CoroutineDispatcher,
    private val searchRequestDtoMapper: SearchRequestDtoMapper,
    private val searchResponseDtoMapper: SearchResponseDtoMapper
) {
    private val logger = LoggerFactory.getLogger(AdSearchController::class.java)

    /**
     * Search for ads across all available sources
     * Requires authentication
     */
    @GetMapping("/search")
    @Operation(summary = "Search for ads across all available sources", description = "Returns ads matching the search criteria from all configured sources")
    suspend fun searchAds(@Valid searchRequest: SearchRequestDto): ResponseEntity<SearchResponseDto> {
        logger.info("Received search request: $searchRequest")

        return withContext(ioDispatcher) {
            val criteria = searchRequestDtoMapper.toDomain(searchRequest)
            val result = adSearchUseCase.searchAdsAcrossSources(criteria)

            val response = searchResponseDtoMapper.toDto(
                result,
                searchRequest.limit,
                searchRequest.offset
            )

            logger.info("Returning ${response.ads.size} ads from ${response.sources.size} sources")
            ResponseEntity.ok(response)
        }
    }

    /**
     * Get available ad sources
     * Public endpoint, no authentication required
     */
    @GetMapping("/sources")
    @Operation(summary = "Get available ad sources", description = "Returns a list of all available ad sources")
    suspend fun getAvailableSources(): ResponseEntity<Map<String, List<String>>> {
        logger.info("Received request for available sources")

        return withContext(ioDispatcher) {
            val sources = adSearchUseCase.getAvailableSources()
            logger.info("Returning ${sources.size} available sources")
            ResponseEntity.ok(mapOf("sources" to sources))
        }
    }
}
