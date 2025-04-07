package com.adsearch.infrastructure.adapter.`in`.web.mapper

import com.adsearch.domain.enum.SortOptionEnum
import com.adsearch.domain.model.SearchCriteria
import com.adsearch.infrastructure.adapter.`in`.web.dto.SearchRequestDto
import org.springframework.stereotype.Component

/**
 * Mapper for converting between SearchCriteria domain model and SearchRequestDto
 */
@Component
class SearchRequestDtoMapper : DtoMapper<SearchRequestDto, SearchCriteria> {
    
    override fun toDto(domainModel: SearchCriteria): SearchRequestDto {
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
    
    override fun toDomain(dto: SearchRequestDto): SearchCriteria {
        return SearchCriteria(
            query = dto.query,
            category = dto.category,
            minPrice = dto.minPrice,
            maxPrice = dto.maxPrice,
            location = dto.location,
            sortBy = parseSortOption(dto.sortBy),
            limit = dto.limit,
            offset = dto.offset
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
