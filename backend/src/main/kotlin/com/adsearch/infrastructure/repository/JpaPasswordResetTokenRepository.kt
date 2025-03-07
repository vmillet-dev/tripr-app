package com.adsearch.infrastructure.repository

import com.adsearch.domain.model.PasswordResetToken
import com.adsearch.domain.port.PasswordResetTokenRepositoryPort
import com.adsearch.infrastructure.repository.entity.PasswordResetTokenEntity
import com.adsearch.infrastructure.repository.jpa.PasswordResetTokenJpaRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.springframework.context.annotation.Primary
import org.springframework.stereotype.Repository

@Repository
@Primary
class JpaPasswordResetTokenRepository(
    private val passwordResetTokenJpaRepository: PasswordResetTokenJpaRepository
) : PasswordResetTokenRepositoryPort {
    
    override suspend fun save(token: PasswordResetToken): PasswordResetToken = withContext(Dispatchers.IO) {
        val entity = passwordResetTokenJpaRepository.save(PasswordResetTokenEntity.fromDomain(token))
        entity.toDomain()
    }
    
    override suspend fun findByToken(token: String): PasswordResetToken? = withContext(Dispatchers.IO) {
        passwordResetTokenJpaRepository.findByToken(token)?.toDomain()
    }
    
    override suspend fun findByUserId(userId: Long): List<PasswordResetToken> = withContext(Dispatchers.IO) {
        passwordResetTokenJpaRepository.findByUserId(userId).map { it.toDomain() }
    }
    
    override suspend fun deleteById(id: Long) = withContext(Dispatchers.IO) {
        passwordResetTokenJpaRepository.deleteById(id)
    }
    
    override suspend fun deleteByUserId(userId: Long) = withContext(Dispatchers.IO) {
        passwordResetTokenJpaRepository.deleteByUserId(userId)
    }
}
