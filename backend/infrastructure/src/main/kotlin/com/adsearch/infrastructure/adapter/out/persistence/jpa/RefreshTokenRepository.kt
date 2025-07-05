package com.adsearch.infrastructure.adapter.out.persistence.jpa

import com.adsearch.infrastructure.adapter.out.persistence.entity.RefreshTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface RefreshTokenRepository : JpaRepository<RefreshTokenEntity, Long> {
    fun findByToken(token: String): RefreshTokenEntity?

    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshTokenEntity rf WHERE rf.token = ?1")
    fun deleteByToken(token: String)

    @Transactional
    @Modifying
    @Query("DELETE FROM RefreshTokenEntity rf WHERE rf.userId = ?1")
    fun deleteByUserId(userId: Long)
}
