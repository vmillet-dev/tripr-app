package com.adsearch.infrastructure.adapter.`in`.web.mapper

import com.adsearch.common.mapper.mapTo
import com.adsearch.common.mapper.mapToList
import com.adsearch.common.mapper.mapToWithCustomMappings
import com.adsearch.domain.enum.SortOptionEnum
import com.adsearch.domain.model.SearchCriteria
import com.adsearch.infrastructure.adapter.`in`.web.dto.SearchRequestDto
import org.springframework.stereotype.Component

/**
 * Mapper for converting between SearchCriteria domain model and SearchRequestDto
 */
@Component
class SearchRequestDtoMapper {
    
    /**
     * Convert a domain SearchCriteria to a SearchRequestDto
     */
    fun toDto(domainModel: SearchCriteria): SearchRequestDto {
        return domainModel.mapToWithCustomMappings<SearchCriteria, SearchRequestDto>(
            mapOf(
                "sortBy" to { source: SearchCriteria -> source.sortBy.name }
            )
        )
    }
    
    /**
     * Convert a SearchRequestDto to a domain SearchCriteria
     */
    fun toDomain(dto: SearchRequestDto): SearchCriteria {
        return dto.mapToWithCustomMappings<SearchRequestDto, SearchCriteria>(
            mapOf(
                "sortBy" to { source: SearchRequestDto -> stringToSortBy(source.sortBy) }
            )
        )
    }
    
    /**
     * Convert a list of domain SearchCriteria to SearchRequestDtos
     */
    fun toDtoList(domainModels: List<SearchCriteria>): List<SearchRequestDto> {
        return domainModels.map { toDto(it) }
    }
    
    /**
     * Convert a list of SearchRequestDtos to domain SearchCriteria
     */
    fun toDomainList(dtos: List<SearchRequestDto>): List<SearchCriteria> {
        return dtos.map { toDomain(it) }
    }
    
    private fun stringToSortBy(sortBy: String?): SortOptionEnum {
        return when (sortBy?.uppercase()) {
            "PRICE_ASC" -> SortOptionEnum.PRICE_ASC
            "PRICE_DESC" -> SortOptionEnum.PRICE_DESC
            "DATE_DESC" -> SortOptionEnum.DATE_DESC
            "DATE_ASC" -> SortOptionEnum.DATE_ASC
            else -> SortOptionEnum.RELEVANCE
        }
    }
}
