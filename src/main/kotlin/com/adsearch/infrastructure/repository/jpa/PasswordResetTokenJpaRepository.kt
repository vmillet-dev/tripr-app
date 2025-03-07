package com.adsearch.infrastructure.repository.jpa

import com.adsearch.infrastructure.repository.entity.PasswordResetTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PasswordResetTokenJpaRepository : JpaRepository<PasswordResetTokenEntity, Long> {
    fun findByToken(token: String): PasswordResetTokenEntity?
    fun findByUserId(userId: Long): List<PasswordResetTokenEntity>
    fun deleteByUserId(userId: Long)
}
