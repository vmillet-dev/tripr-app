package com.adsearch.infrastructure.adapter.persistence

import com.adsearch.domain.model.PasswordResetToken
import com.adsearch.domain.port.repository.PasswordResetTokenRepositoryPort
import com.adsearch.infrastructure.repository.JpaPasswordResetTokenRepository
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

/**
 * Adapter implementation for PasswordResetTokenRepositoryPort that delegates to the original JpaPasswordResetTokenRepository
 * This follows the adapter pattern in hexagonal architecture
 */
@Repository
@Primary
class JpaPasswordResetTokenRepositoryAdapter(private val jpaPasswordResetTokenRepository: JpaPasswordResetTokenRepository) : PasswordResetTokenRepositoryPort {
    
    override suspend fun findByToken(token: String): PasswordResetToken? {
        return jpaPasswordResetTokenRepository.findByToken(token)
    }
    
    override suspend fun findByUserId(userId: Long): List<PasswordResetToken> {
        return jpaPasswordResetTokenRepository.findByUserId(userId)
    }
    
    override suspend fun save(token: PasswordResetToken): PasswordResetToken {
        return jpaPasswordResetTokenRepository.save(token)
    }
    
    override suspend fun deleteById(id: Long) {
        jpaPasswordResetTokenRepository.deleteById(id)
    }
    
    override suspend fun deleteByUserId(userId: Long) {
        jpaPasswordResetTokenRepository.deleteByUserId(userId)
    }
}
