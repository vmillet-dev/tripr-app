package com.adsearch.infrastructure.adapter.`in`.web.mapper

import com.adsearch.domain.enum.SortOptionEnum
import com.adsearch.domain.model.SearchCriteria
import com.adsearch.infrastructure.adapter.`in`.web.dto.SearchRequestDto
import org.mapstruct.Mapper
import org.mapstruct.Named
import org.springframework.stereotype.Component

/**
 * MapStruct mapper for converting between SearchCriteria domain model and SearchRequestDto
 */
@Component
class SearchRequestDtoMapper {
    
    /**
     * Convert a domain SearchCriteria to a SearchRequestDto
     */
    fun toDto(domainModel: SearchCriteria): SearchRequestDto {
        return SearchRequestDto(
            query = domainModel.query,
            category = domainModel.category,
            minPrice = domainModel.minPrice,
            maxPrice = domainModel.maxPrice,
            location = domainModel.location,
            sortBy = domainModel.sortBy.name,
            limit = domainModel.limit,
            offset = domainModel.offset
        )
    }
    
    /**
     * Convert a SearchRequestDto to a domain SearchCriteria
     */
    fun toDomain(dto: SearchRequestDto): SearchCriteria {
        return SearchCriteria(
            query = dto.query,
            category = dto.category,
            minPrice = dto.minPrice,
            maxPrice = dto.maxPrice,
            location = dto.location,
            sortBy = stringToSortBy(dto.sortBy),
            limit = dto.limit,
            offset = dto.offset
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
