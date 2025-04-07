package com.adsearch.infrastructure.adapter.out.security.mapper

import com.adsearch.domain.model.User
import com.adsearch.infrastructure.security.model.JwtUserDetails
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.stereotype.Component

/**
 * Mapper for converting between domain User and infrastructure JwtUserDetails
 */
@Component
class JwtUserDetailsMapper {
    
    /**
     * Convert from domain User to infrastructure JwtUserDetails
     */
    fun toJwtUserDetails(user: User): JwtUserDetails {
        val authorities = user.roles.map { SimpleGrantedAuthority(it) }
        return JwtUserDetails(
            id = user.id,
            username = user.username,
            hash = user.password,
            authorities = authorities
        )
    }
    
    /**
     * Convert from infrastructure JwtUserDetails to domain User
     */
    fun toUser(userDetails: JwtUserDetails): User {
        return User(
            id = userDetails.id,
            username = userDetails.username,
            password = userDetails.password,
            roles = userDetails.authorities.map { it.authority }.toMutableList(),
            enabled = true
        )
    }
}
