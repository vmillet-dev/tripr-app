package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.port.RefreshTokenPersistencePort
import com.adsearch.infrastructure.adapter.out.persistence.entity.RefreshTokenEntity
import com.adsearch.infrastructure.adapter.out.persistence.jpa.RefreshTokenRepository
import com.adsearch.infrastructure.adapter.out.persistence.mapper.RefreshTokenEntityMapper
import org.springframework.stereotype.Component

@Component
class RefreshTokenPersistenceAdapter(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val refreshTokenEntityMapper: RefreshTokenEntityMapper
) : RefreshTokenPersistencePort {

    override fun save(refreshToken: RefreshToken): RefreshToken {
        val entity = refreshTokenEntityMapper.toEntity(refreshToken)
        return refreshTokenEntityMapper.toDomain(refreshTokenRepository.save(entity))
    }

    override fun findByUserId(userId: Long): List<RefreshToken> {
        // Since the JPA repository doesn't have a findByUserId method that returns a list,
        // we'll use findAll and filter the results
        return refreshTokenRepository.findAll()
            .filter { it.userId == userId }
            .map(refreshTokenEntityMapper::toDomain)
    }

    override fun findByToken(token: String): RefreshToken? {
        return refreshTokenRepository.findByToken(token)?.let(refreshTokenEntityMapper::toDomain)
    }

    override fun deleteById(id: Long) {
        refreshTokenRepository.deleteById(id)
    }

    override fun deleteByUserId(userId: Long) {
        refreshTokenRepository.deleteByUserId(userId)
    }
}
