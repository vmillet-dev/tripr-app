package com.adsearch.infrastructure.adapter.persistence

import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.port.repository.RefreshTokenRepositoryPort
import com.adsearch.infrastructure.repository.JpaRefreshTokenRepository
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

/**
 * Adapter implementation for RefreshTokenRepositoryPort that delegates to the original JpaRefreshTokenRepository
 * This follows the adapter pattern in hexagonal architecture
 */
@Repository
@Primary
class JpaRefreshTokenRepositoryAdapter(private val jpaRefreshTokenRepository: JpaRefreshTokenRepository) : RefreshTokenRepositoryPort {
    
    override suspend fun findByToken(token: String): RefreshToken? {
        return jpaRefreshTokenRepository.findByToken(token)
    }
    
    override suspend fun findByUserId(userId: Long): List<RefreshToken> {
        return jpaRefreshTokenRepository.findByUserId(userId)
    }
    
    override suspend fun save(refreshToken: RefreshToken): RefreshToken {
        return jpaRefreshTokenRepository.save(refreshToken)
    }
    
    override suspend fun deleteById(id: Long) {
        jpaRefreshTokenRepository.deleteById(id)
    }
    
    override suspend fun deleteByUserId(userId: Long) {
        jpaRefreshTokenRepository.deleteByUserId(userId)
    }
}
