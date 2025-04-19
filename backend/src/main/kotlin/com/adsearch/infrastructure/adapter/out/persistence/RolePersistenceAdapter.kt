package com.adsearch.infrastructure.adapter.out.persistence

import com.adsearch.domain.model.Role
import com.adsearch.domain.model.RoleType
import com.adsearch.domain.port.RolePersistencePort
import com.adsearch.infrastructure.adapter.out.persistence.jpa.RoleRepository
import com.adsearch.infrastructure.adapter.out.persistence.mapper.RoleEntityMapper
import org.springframework.stereotype.Component

@Component
class RolePersistenceAdapter(
    private val roleRepository: RoleRepository,
    private val roleEntityMapper: RoleEntityMapper
) : RolePersistencePort {

    override fun findByName(name: RoleType): Role? =
        roleRepository.findByName(name)?.let(roleEntityMapper::toDomain)

    override fun save(role: Role): Role =
        roleRepository.save(roleEntityMapper.fromDomain(role)).let(roleEntityMapper::toDomain)

    override fun findAll(): List<Role> =
        roleRepository.findAll().map(roleEntityMapper::toDomain)
}
