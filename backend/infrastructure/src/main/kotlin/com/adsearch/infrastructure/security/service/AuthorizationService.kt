package com.adsearch.infrastructure.security.service

import org.springframework.stereotype.Component

@Component("authorization")
class AuthorizationService {

    fun permitAll(): Boolean {
        return true
    }
}
