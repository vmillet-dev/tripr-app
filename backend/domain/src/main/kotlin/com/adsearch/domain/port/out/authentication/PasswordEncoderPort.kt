package com.adsearch.domain.port.out.authentication

interface PasswordEncoderPort {
    fun encode(rawPassword: String): String
}
