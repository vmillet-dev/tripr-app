package com.adsearch.domain.port.out.security

interface PasswordEncoderPort {
    fun encode(rawPassword: String): String
}
