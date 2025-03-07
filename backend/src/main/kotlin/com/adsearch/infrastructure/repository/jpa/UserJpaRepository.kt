package com.adsearch.infrastructure.repository.jpa

import com.adsearch.infrastructure.repository.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserJpaRepository : JpaRepository<UserEntity, Long> {
    fun findByUsername(username: String): UserEntity?
}
