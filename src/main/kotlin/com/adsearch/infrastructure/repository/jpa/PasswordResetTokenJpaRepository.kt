package com.adsearch.infrastructure.repository.jpa

import com.adsearch.infrastructure.repository.entity.PasswordResetTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.UUID

@Repository
interface PasswordResetTokenJpaRepository : JpaRepository<PasswordResetTokenEntity, UUID> {
    fun findByToken(token: String): PasswordResetTokenEntity?
    fun findByUserId(userId: UUID): List<PasswordResetTokenEntity>
    fun deleteByUserId(userId: UUID)
}
