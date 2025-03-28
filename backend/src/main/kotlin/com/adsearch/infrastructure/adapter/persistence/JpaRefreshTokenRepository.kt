package com.adsearch.infrastructure.adapter.persistence

import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.port.repository.RefreshTokenRepositoryPort
import com.adsearch.infrastructure.repository.entity.RefreshTokenEntity
import com.adsearch.infrastructure.repository.jpa.RefreshTokenJpaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

/**
 * Adapter implementation of RefreshTokenRepositoryPort using JPA
 */
@Repository
@Primary
class JpaRefreshTokenRepository(
    private val refreshTokenJpaRepository: RefreshTokenJpaRepository
) : RefreshTokenRepositoryPort {
    
    override suspend fun save(refreshToken: RefreshToken): RefreshToken = withContext(Dispatchers.IO) {
        val entity = refreshTokenJpaRepository.save(RefreshTokenEntity.fromDomain(refreshToken))
        entity.toDomain()
    }
    
    override suspend fun findByUserId(userId: Long): List<RefreshToken> = withContext(Dispatchers.IO) {
        refreshTokenJpaRepository.findAll()
            .filter { it.userId == userId }
            .map { it.toDomain() }
    }
    
    override suspend fun findByToken(token: String): RefreshToken? = withContext(Dispatchers.IO) {
        refreshTokenJpaRepository.findByToken(token)?.toDomain()
    }
    
    override suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        refreshTokenJpaRepository.deleteById(id)
    }
    
    override suspend fun deleteByUserId(userId: Long) = withContext(Dispatchers.IO) {
        refreshTokenJpaRepository.deleteByUserId(userId)
    }
}
