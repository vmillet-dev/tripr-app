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

    override fun save(dom: PasswordResetTokenDom) {
        passwordResetTokenRepository.save(passwordResetTokenEntityMapper.toEntity(dom))
    }

    override fun findByToken(token: String): PasswordResetTokenDom? {
        return passwordResetTokenRepository.findByToken(token)?.let(passwordResetTokenEntityMapper::toDomain)
    }

    override fun deleteByToken(token: String) {
        passwordResetTokenRepository.deleteByToken(token)
    }

    override fun deleteByUserId(userId: Long) {
        passwordResetTokenRepository.deleteByUserId(userId)
    }
}
