package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.model.PasswordResetToken
import com.adsearch.domain.port.PasswordResetTokenPersistencePort
import com.adsearch.infrastructure.adapter.out.persistence.entity.PasswordResetTokenEntity
import com.adsearch.infrastructure.adapter.out.persistence.jpa.PasswordResetTokenRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component

@Component
class PasswordResetTokenPersistenceAdapter(private val passwordResetTokenRepository: PasswordResetTokenRepository) : PasswordResetTokenPersistencePort {

    override suspend fun save(token: PasswordResetToken): PasswordResetToken = withContext(Dispatchers.IO) {
        val entity = passwordResetTokenRepository.save(PasswordResetTokenEntity.fromDomain(token))
        entity.toDomain()
    }

    override suspend fun findByToken(token: String): PasswordResetToken? = withContext(Dispatchers.IO) {
        passwordResetTokenRepository.findByToken(token)?.toDomain()
    }

    override suspend fun findByUserId(userId: Long): List<PasswordResetToken> = withContext(Dispatchers.IO) {
        passwordResetTokenRepository.findByUserId(userId).map { it.toDomain() }
    }

    override suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        passwordResetTokenRepository.deleteById(id)
    }

    override suspend fun deleteByUserId(userId: Long) = withContext(Dispatchers.IO) {
        passwordResetTokenRepository.deleteByUserId(userId)
    }
}
