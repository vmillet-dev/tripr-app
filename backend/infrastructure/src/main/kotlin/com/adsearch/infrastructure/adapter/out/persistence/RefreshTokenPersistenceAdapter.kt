package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.model.RefreshTokenDom
import com.adsearch.domain.model.UserDom
import com.adsearch.domain.port.out.RefreshTokenPersistencePort
import com.adsearch.infrastructure.adapter.out.persistence.jpa.RefreshTokenRepository
import com.adsearch.infrastructure.adapter.out.persistence.mapper.RefreshTokenEntityMapper
import org.springframework.stereotype.Component

@Component
class RefreshTokenPersistenceAdapter(
    private val refreshTokenRepository: RefreshTokenRepository,
    private val refreshTokenEntityMapper: RefreshTokenEntityMapper
) : RefreshTokenPersistencePort {

    override fun save(refreshTokenDom: RefreshTokenDom) {
        refreshTokenRepository.save(refreshTokenEntityMapper.fromDomain(refreshTokenDom))
    }

    override fun findByToken(token: String): RefreshTokenDom? {
        return refreshTokenRepository.findByToken(token)?.let(refreshTokenEntityMapper::toDomain)
    }

    override fun deleteByToken(token: String) {
        refreshTokenRepository.deleteByToken(token)
    }

    override fun deleteByUser(user: UserDom) {
        refreshTokenRepository.deleteByUserUsername(user.username)
    }
}
