package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.port.RefreshTokenPersistencePort
import com.adsearch.infrastructure.adapter.out.persistence.jpa.RefreshTokenRepository
import com.adsearch.infrastructure.adapter.out.persistence.mapper.RefreshTokenEntityMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component

@Component
class RefreshTokenPersistenceAdapter(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val refreshTokenEntityMapper: RefreshTokenEntityMapper
) : RefreshTokenPersistencePort {

    override suspend fun save(refreshToken: RefreshToken): RefreshToken = withContext(Dispatchers.IO) {
        val entity = refreshTokenRepository.save(refreshTokenEntityMapper.toEntity(refreshToken))
        refreshTokenEntityMapper.toDomain(entity)
    }

    override suspend fun findByUserId(userId: Long): List<RefreshToken> = withContext(Dispatchers.IO) {
        // Since the JPA repository doesn't have a findByUserId method that returns a list,
        // we'll use findAll and filter the results
        refreshTokenEntityMapper.toDomainList(
            refreshTokenRepository.findAll().filter { it.userId == userId }
        )
    }

    override suspend fun findByToken(token: String): RefreshToken? = withContext(Dispatchers.IO) {
        refreshTokenRepository.findByToken(token)?.let { refreshTokenEntityMapper.toDomain(it) }
    }

    override suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        refreshTokenRepository.deleteById(id)
    }

    override suspend fun deleteByUserId(userId: Long) = withContext(Dispatchers.IO) {
        refreshTokenRepository.deleteByUserId(userId)
    }
}
