package com.adsearch.domain.port

import com.adsearch.domain.model.Role
import com.adsearch.domain.model.RoleType

/**
 * Port for role persistence operations
 */
interface RolePersistencePort {
    fun findByName(name: RoleType): Role?
    fun save(role: Role): Role
    fun findAll(): List<Role>
}
