package com.adsearch.application.model

import com.adsearch.domain.model.UserDom

/**
 * Result data class for refresh token operation
 */
data class RefreshResult(
    val user: UserDom,
    val accessToken: String
)
