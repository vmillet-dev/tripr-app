package com.adsearch.infrastructure.adapter.out.persistence.jpa

import com.adsearch.domain.model.enums.TokenTypeEnum
import com.adsearch.infrastructure.adapter.out.persistence.entity.TokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface TokenRepository : JpaRepository<TokenEntity, Long> {
    fun findByTokenAndType(token: String, type: TokenTypeEnum): TokenEntity?

    @Transactional
    @Modifying
    @Query("DELETE FROM TokenEntity t WHERE t.token = ?1 AND t.type = ?2")
    fun deleteByTokenAndType(token: String, type: TokenTypeEnum)

    @Transactional
    @Modifying
    @Query("DELETE FROM TokenEntity t WHERE t.userId = ?1 AND t.type = ?2")
    fun deleteByUserIdAndType(userId: Long, type: TokenTypeEnum)
}
