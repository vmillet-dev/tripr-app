package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.model.RefreshTokenDom
import com.adsearch.domain.port.spi.RefreshTokenPersistencePort
import com.adsearch.infrastructure.adapter.out.persistence.jpa.RefreshTokenRepository
import com.adsearch.infrastructure.adapter.out.persistence.mapper.RefreshTokenEntityMapper
import org.springframework.stereotype.Component

@Component
class RefreshTokenPersistenceAdapter(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val refreshTokenEntityMapper: RefreshTokenEntityMapper
) : RefreshTokenPersistencePort {

    override fun save(refreshTokenDom: RefreshTokenDom): RefreshTokenDom {
        val entity = refreshTokenEntityMapper.fromDomain(refreshTokenDom)
        return refreshTokenEntityMapper.toDomain(refreshTokenRepository.save(entity))
    }

    override fun findByUserId(userId: Long): List<RefreshTokenDom> {
        return refreshTokenRepository.findByUserId(userId).map(refreshTokenEntityMapper::toDomain)
    }

    override fun findByToken(token: String): RefreshTokenDom? {
        return refreshTokenRepository.findByToken(token)?.let(refreshTokenEntityMapper::toDomain)
    }

    override fun deleteById(id: Long) {
        refreshTokenRepository.deleteById(id)
    }

    override fun deleteByUserId(userId: Long) {
        refreshTokenRepository.deleteByUserId(userId)
    }
}
