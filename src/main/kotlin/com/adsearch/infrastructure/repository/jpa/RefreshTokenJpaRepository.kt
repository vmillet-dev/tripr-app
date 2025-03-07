package com.adsearch.infrastructure.repository.jpa

import com.adsearch.infrastructure.repository.entity.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface RefreshTokenJpaRepository : JpaRepository<RefreshTokenEntity, UUID> {
    fun findByToken(token: String): RefreshTokenEntity?
    fun deleteByUserId(userId: UUID)
}
