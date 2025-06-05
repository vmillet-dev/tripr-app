package com.adsearch.infrastructure.adapter.`in`.web.mapper

import com.adsearch.application.model.LoginResult
import com.adsearch.application.model.RefreshResult
import com.adsearch.infrastructure.adapter.`in`.web.dto.AuthResponseDto
import org.springframework.stereotype.Component

/**
 * Mapper for authentication response objects
 */
@Component
class AuthResponseMapper {
    
    fun toDto(loginResult: LoginResult): AuthResponseDto {
        return AuthResponseDto(
            accessToken = loginResult.accessToken,
            username = loginResult.user.username,
            roles = loginResult.user.roles
        )
    }
    
    fun toDto(refreshResult: RefreshResult): AuthResponseDto {
        return AuthResponseDto(
            accessToken = refreshResult.accessToken,
            username = refreshResult.user.username,
            roles = refreshResult.user.roles
        )
    }
}
