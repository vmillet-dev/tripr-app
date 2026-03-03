package com.adsearch.infrastructure.security

import com.adsearch.infrastructure.adapter.out.persistence.entity.UserEntity
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

data class UserPrincipal(
    val id: Long,
    private val username: String,
    private val password: String,
    private val authorities: Collection<GrantedAuthority>,
    private val isEnabled: Boolean
) : UserDetails {

    override fun getAuthorities(): Collection<GrantedAuthority> = authorities

    override fun getPassword(): String = password

    override fun getUsername(): String = username

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = isEnabled

    companion object {
        fun create(user: UserEntity): UserPrincipal {
            return UserPrincipal(
                id = user.id,
                username = user.username,
                password = user.password,
                authorities = user.roles.flatMap { listOf(SimpleGrantedAuthority(it.type)) },
                isEnabled = user.enabled
            )
        }
    }
}
