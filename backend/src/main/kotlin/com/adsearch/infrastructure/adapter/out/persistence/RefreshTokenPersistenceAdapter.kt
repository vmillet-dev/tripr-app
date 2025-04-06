package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.port.RefreshTokenPersistencePort
import com.adsearch.infrastructure.adapter.out.persistence.entity.RefreshTokenEntity
import com.adsearch.infrastructure.adapter.out.persistence.jpa.RefreshTokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component

@Component
class RefreshTokenPersistenceAdapter(private val refreshTokenRepository: RefreshTokenRepository) : RefreshTokenPersistencePort {

    override suspend fun save(refreshToken: RefreshToken): RefreshToken = withContext(Dispatchers.IO) {
        val entity = refreshTokenRepository.save(RefreshTokenEntity.fromDomain(refreshToken))
        entity.toDomain()
    }

    override suspend fun findByUserId(userId: Long): List<RefreshToken> = withContext(Dispatchers.IO) {
        // Since the JPA repository doesn't have a findByUserId method that returns a list,
        // we'll use findAll and filter the results
        refreshTokenRepository.findAll()
            .filter { it.userId == userId }
            .map { it.toDomain() }
    }

    override suspend fun findByToken(token: String): RefreshToken? = withContext(Dispatchers.IO) {
        refreshTokenRepository.findByToken(token)?.toDomain()
    }

    override suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        refreshTokenRepository.deleteById(id)
    }

    override suspend fun deleteByUserId(userId: Long) = withContext(Dispatchers.IO) {
        refreshTokenRepository.deleteByUserId(userId)
    }
}
