package com.adsearch.infrastructure.adapter.out.persistence.jpa

import com.adsearch.infrastructure.adapter.out.persistence.entity.PasswordResetTokenEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface PasswordResetTokenRepository : JpaRepository<PasswordResetTokenEntity, Long> {
    fun findByToken(token: String): PasswordResetTokenEntity?

    @Transactional
    @Modifying
    @Query("DELETE FROM PasswordResetTokenEntity prt WHERE prt.token = ?1")
    fun deleteByToken(token: String)

    @Transactional
    @Modifying
    @Query("DELETE FROM PasswordResetTokenEntity prt WHERE prt.user.username = ?1")
    fun deleteByUserUsername(username: String)
}
