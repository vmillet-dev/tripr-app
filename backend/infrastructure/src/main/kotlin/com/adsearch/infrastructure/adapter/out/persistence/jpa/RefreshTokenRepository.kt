package com.adsearch.infrastructure.adapter.out.persistence.jpa

import com.adsearch.infrastructure.adapter.out.persistence.entity.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshTokenEntity, Long> {
    fun findByToken(token: String): RefreshTokenEntity?
    fun deleteByToken(token: String)
    fun deleteByUserUsername(username: String)
}
