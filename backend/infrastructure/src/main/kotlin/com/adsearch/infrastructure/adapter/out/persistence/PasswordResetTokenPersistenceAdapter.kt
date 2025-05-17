package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.model.PasswordResetTokenDom
import com.adsearch.domain.port.out.PasswordResetTokenPersistencePort
import com.adsearch.infrastructure.adapter.out.persistence.jpa.PasswordResetTokenRepository
import com.adsearch.infrastructure.adapter.out.persistence.mapper.PasswordResetTokenEntityMapper
import org.springframework.stereotype.Component

@Component
class PasswordResetTokenPersistenceAdapter(
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val passwordResetTokenEntityMapper: PasswordResetTokenEntityMapper
) : PasswordResetTokenPersistencePort {

    override fun save(token: PasswordResetTokenDom): PasswordResetTokenDom {
        val entity = passwordResetTokenEntityMapper.fromDomain(token)
        return passwordResetTokenEntityMapper.toDomain(passwordResetTokenRepository.save(entity))
    }

    override fun findByToken(token: String): PasswordResetTokenDom? {
        return passwordResetTokenRepository.findByToken(token)?.let(passwordResetTokenEntityMapper::toDomain)
    }

    override fun findByUserId(userId: Long): List<PasswordResetTokenDom> {
        return passwordResetTokenRepository.findByUserId(userId).map(passwordResetTokenEntityMapper::toDomain)
    }

    override fun deleteById(id: Long) {
        passwordResetTokenRepository.deleteById(id)
    }

    override fun deleteByUserId(userId: Long) {
        passwordResetTokenRepository.deleteByUserId(userId)
    }
}
