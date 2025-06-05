package com.adsearch.application.model

import com.adsearch.domain.model.UserDom

/**
 * Result data class for login operation
 */
data class LoginResult(
    val user: UserDom,
    val accessToken: String,
    val refreshToken: String
)
