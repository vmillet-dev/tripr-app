package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.model.PasswordResetToken
import com.adsearch.domain.port.PasswordResetTokenPersistencePort
import com.adsearch.infrastructure.adapter.out.persistence.entity.PasswordResetTokenEntity
import com.adsearch.infrastructure.adapter.out.persistence.jpa.PasswordResetTokenRepository
import com.adsearch.infrastructure.adapter.out.persistence.mapper.PasswordResetTokenEntityMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component

@Component
class PasswordResetTokenPersistenceAdapter(
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val passwordResetTokenEntityMapper: PasswordResetTokenEntityMapper
) : PasswordResetTokenPersistencePort {

    override suspend fun save(token: PasswordResetToken): PasswordResetToken = withContext(Dispatchers.IO) {
        val entity = passwordResetTokenRepository.save(passwordResetTokenEntityMapper.toEntity(token))
        passwordResetTokenEntityMapper.toDomain(entity)
    }

    override suspend fun findByToken(token: String): PasswordResetToken? = withContext(Dispatchers.IO) {
        passwordResetTokenRepository.findByToken(token)?.let { passwordResetTokenEntityMapper.toDomain(it) }
    }

    override suspend fun findByUserId(userId: Long): List<PasswordResetToken> = withContext(Dispatchers.IO) {
        passwordResetTokenEntityMapper.toDomainList(passwordResetTokenRepository.findByUserId(userId))
    }

    override suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        passwordResetTokenRepository.deleteById(id)
    }

    override suspend fun deleteByUserId(userId: Long) = withContext(Dispatchers.IO) {
        passwordResetTokenRepository.deleteByUserId(userId)
    }
}
