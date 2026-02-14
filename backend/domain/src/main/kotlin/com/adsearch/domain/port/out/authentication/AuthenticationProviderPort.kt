package com.adsearch.domain.port.out.authentication

interface AuthenticationProviderPort {
    fun authenticate(username: String, password: String): String
}
