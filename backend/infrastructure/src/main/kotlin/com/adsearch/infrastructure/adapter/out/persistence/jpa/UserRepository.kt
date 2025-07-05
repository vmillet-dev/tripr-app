package com.adsearch.infrastructure.adapter.out.persistence.jpa

import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import org.springframework.transaction.annotation.Transactional

@Repository
interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByUsername(username: String): UserEntity?
    fun findByEmail(email: String): UserEntity?

    @Transactional
    @Modifying
    @Query("UPDATE UserEntity u SET u.password = :password WHERE u.username = :username")
    fun updatePassword(@Param("username") username: String, @Param("password") password: String): Int
}
