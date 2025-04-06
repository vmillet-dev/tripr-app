package com.adsearch.infrastructure.adapter.out.persistence.jpa

import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository : JpaRepository<UserEntity, Long> {
    fun findByUsername(username: String): UserEntity?
}
