package com.adsearch.infrastructure.adapter.out.persistence.jpa

import com.adsearch.domain.model.RoleType
import com.adsearch.infrastructure.adapter.out.persistence.entity.RoleEntity
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface RoleRepository : JpaRepository<RoleEntity, Long> {
    fun findByName(name: RoleType): RoleEntity?
}
