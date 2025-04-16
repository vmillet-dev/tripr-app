package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.model.PasswordResetToken
import com.adsearch.domain.port.PasswordResetTokenPersistencePort
import com.adsearch.infrastructure.adapter.out.persistence.entity.PasswordResetTokenEntity
import com.adsearch.infrastructure.adapter.out.persistence.jpa.PasswordResetTokenRepository
import org.springframework.stereotype.Component

@Component
class PasswordResetTokenPersistenceAdapter(private val passwordResetTokenRepository: PasswordResetTokenRepository) : PasswordResetTokenPersistencePort {

    override fun save(token: PasswordResetToken): PasswordResetToken {
        val entity = passwordResetTokenRepository.save(PasswordResetTokenEntity.fromDomain(token))
        return entity.toDomain()
    }

    override fun findByToken(token: String): PasswordResetToken? {
        return passwordResetTokenRepository.findByToken(token)?.toDomain()
    }

    override fun findByUserId(userId: Long): List<PasswordResetToken> {
        return passwordResetTokenRepository.findByUserId(userId).map { it.toDomain() }
    }

    override fun deleteById(id: Long) {
        passwordResetTokenRepository.deleteById(id)
    }

    override fun deleteByUserId(userId: Long) {
        passwordResetTokenRepository.deleteByUserId(userId)
    }
}
