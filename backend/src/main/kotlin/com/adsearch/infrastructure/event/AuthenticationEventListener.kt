package com.adsearch.infrastructure.event

import com.adsearch.domain.event.auth.AuthenticationEvent
import org.slf4j.LoggerFactory
import org.springframework.context.event.EventListener
import org.springframework.stereotype.Component

/**
 * Listener for authentication events
 */
@Component
class AuthenticationEventListener {
    
    private val logger = LoggerFactory.getLogger(AuthenticationEventListener::class.java)
    
    @EventListener
    fun handleUserLoggedIn(event: AuthenticationEvent.UserLoggedIn) {
        logger.info("User logged in: {}", event.username)
    }
    
    @EventListener
    fun handleUserLoggedOut(event: AuthenticationEvent.UserLoggedOut) {
        logger.info("User logged out: {}", event.username)
    }
    
    @EventListener
    fun handleUserRegistered(event: AuthenticationEvent.UserRegistered) {
        logger.info("User registered: {}", event.username)
    }
    
    @EventListener
    fun handlePasswordResetRequested(event: AuthenticationEvent.PasswordResetRequested) {
        logger.info("Password reset requested for user: {}", event.username)
    }
    
    @EventListener
    fun handlePasswordReset(event: AuthenticationEvent.PasswordReset) {
        logger.info("Password reset for user: {}", event.username)
    }
}
