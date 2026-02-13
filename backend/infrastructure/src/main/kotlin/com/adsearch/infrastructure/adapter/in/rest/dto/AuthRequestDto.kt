package com.adsearch.infrastructure.adapter.`in`.rest.dto

import com.fasterxml.jackson.annotation.JsonCreator
import com.fasterxml.jackson.annotation.JsonProperty

/**
 * DTO for authentication request
 */
data class AuthRequestDto @JsonCreator constructor(
    @JsonProperty("username") val username: String,
    @JsonProperty("password") val password: String
)
