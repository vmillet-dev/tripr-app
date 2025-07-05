package com.adsearch.infrastructure.adapter.out.persistence.jpa

import com.adsearch.infrastructure.adapter.out.persistence.entity.PasswordResetTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PasswordResetTokenRepository : JpaRepository<PasswordResetTokenEntity, Long> {
    fun findByToken(token: String): PasswordResetTokenEntity?
    fun deleteByToken(token: String)
    fun deleteByUserUsername(username: String)
}
