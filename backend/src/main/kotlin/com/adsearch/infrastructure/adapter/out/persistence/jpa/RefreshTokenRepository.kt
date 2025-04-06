package com.adsearch.infrastructure.adapter.out.persistence.jpa

import com.adsearch.infrastructure.adapter.out.persistence.entity.RefreshTokenEntity
import jakarta.transaction.Transactional
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
@Transactional
interface RefreshTokenRepository : JpaRepository<RefreshTokenEntity, Long> {
    fun findByToken(token: String): RefreshTokenEntity?
    fun deleteByUserId(userId: Long)
}
