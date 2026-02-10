package com.adsearch.infrastructure.adapter.out.security

import com.adsearch.domain.port.out.security.PasswordEncoderPort
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Component

@Component
class PasswordEncoderAdapter(private val passwordEncoder: PasswordEncoder): PasswordEncoderPort {
    override fun encode(rawPassword: String): String = passwordEncoder.encode(rawPassword)
}
