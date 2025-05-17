package com.adsearch.infrastructure.security.model

import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.userdetails.User

class JwtUserDetails(
    val id: Long,
    username: String,
    hash: String?,
    authorities: Collection<GrantedAuthority>
) : User(username, hash, authorities)
