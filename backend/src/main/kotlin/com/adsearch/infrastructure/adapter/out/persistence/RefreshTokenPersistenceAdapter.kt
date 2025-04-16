package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.port.RefreshTokenPersistencePort
import com.adsearch.infrastructure.adapter.out.persistence.entity.RefreshTokenEntity
import com.adsearch.infrastructure.adapter.out.persistence.jpa.RefreshTokenRepository
import org.springframework.stereotype.Component

@Component
class RefreshTokenPersistenceAdapter(private val refreshTokenRepository: RefreshTokenRepository) : RefreshTokenPersistencePort {

    override fun save(refreshToken: RefreshToken): RefreshToken {
        val entity = refreshTokenRepository.save(RefreshTokenEntity.fromDomain(refreshToken))
        return entity.toDomain()
    }

    override fun findByUserId(userId: Long): List<RefreshToken> {
        // Since the JPA repository doesn't have a findByUserId method that returns a list,
        // we'll use findAll and filter the results
        return refreshTokenRepository.findAll()
            .filter { it.userId == userId }
            .map { it.toDomain() }
    }

    override fun findByToken(token: String): RefreshToken? {
        return refreshTokenRepository.findByToken(token)?.toDomain()
    }

    override fun deleteById(id: Long) {
        refreshTokenRepository.deleteById(id)
    }

    override fun deleteByUserId(userId: Long) {
        refreshTokenRepository.deleteByUserId(userId)
    }
}
