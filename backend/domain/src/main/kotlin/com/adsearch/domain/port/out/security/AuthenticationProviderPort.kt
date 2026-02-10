package com.adsearch.domain.port.out.security

interface AuthenticationProviderPort {
    fun authenticate(username: String, password: String): String
}
