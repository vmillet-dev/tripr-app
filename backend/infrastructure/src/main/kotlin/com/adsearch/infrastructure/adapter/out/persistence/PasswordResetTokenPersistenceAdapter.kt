package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.model.PasswordResetTokenDom
import com.adsearch.domain.model.UserDom
import com.adsearch.domain.port.out.PasswordResetTokenPersistencePort
import com.adsearch.infrastructure.adapter.out.persistence.jpa.PasswordResetTokenRepository
import com.adsearch.infrastructure.adapter.out.persistence.jpa.UserRepository
import com.adsearch.infrastructure.adapter.out.persistence.mapper.PasswordResetTokenEntityMapper
import org.springframework.stereotype.Component

@Component
class PasswordResetTokenPersistenceAdapter(
    private val passwordResetTokenRepository: PasswordResetTokenRepository,
    private val passwordResetTokenEntityMapper: PasswordResetTokenEntityMapper,
    private val userRepository: UserRepository
) : PasswordResetTokenPersistencePort {

    override fun save(dom: PasswordResetTokenDom) {
        val entity = passwordResetTokenEntityMapper.toEntity(dom)
        entity.user = userRepository.findByUsername(dom.user.username)!!
        passwordResetTokenRepository.save(entity)
    }

    override fun findByToken(token: String): PasswordResetTokenDom? {
        return passwordResetTokenRepository.findByToken(token)?.let(passwordResetTokenEntityMapper::toDomain)
    }

    override fun deleteByToken(token: String) {
        passwordResetTokenRepository.deleteByToken(token)
    }

    override fun deleteByUser(user: UserDom) {
        passwordResetTokenRepository.deleteByUserUsername(user.username)
    }
}
