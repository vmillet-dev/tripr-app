package com.adsearch.infrastructure.adapter.`in`.rest.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * DTO for authentication response
 */
data class AuthResponseDto @JsonCreator constructor(
    @JsonProperty("accessToken") val accessToken: String
)
