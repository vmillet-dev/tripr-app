package com.adsearch.infrastructure.adapter.`in`.web.mapper

import com.adsearch.application.model.LoginResult
import com.adsearch.application.model.RefreshResult
import com.adsearch.infrastructure.adapter.`in`.web.dto.AuthResponseDto
import io.mcarle.konvert.api.Konvert
import io.mcarle.konvert.api.Konverter
import io.mcarle.konvert.api.Mapping
import io.mcarle.konvert.injector.spring.KComponent

/**
 * Mapper for authentication response objects
 */
@Konverter
@KComponent
interface AuthResponseMapper {
    
    @Konvert(mappings=[
        Mapping(target = "username", expression = "it.user.username"),
        Mapping(target = "roles", expression = "it.user.roles")
    ])
    fun toDto(loginResult: LoginResult): AuthResponseDto
    
    @Konvert(mappings=[
        Mapping(target = "username", expression = "it.user.username"),
        Mapping(target = "roles", expression = "it.user.roles")
    ])
    fun toDto(refreshResult: RefreshResult): AuthResponseDto
}
