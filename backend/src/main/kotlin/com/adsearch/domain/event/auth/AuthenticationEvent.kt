package com.adsearch.domain.event.auth

import com.adsearch.domain.event.DomainEvent
import java.time.Instant

/**
 * Base class for authentication-related events
 */
sealed class AuthenticationEvent(
    override val timestamp: Instant = Instant.now(),
    override val type: String
) : DomainEvent {
    
    /**
     * Event fired when a user successfully logs in
     */
    data class UserLoggedIn(
        val username: String,
        override val timestamp: Instant = Instant.now()
    ) : AuthenticationEvent(timestamp, "user.logged_in")
    
    /**
     * Event fired when a user logs out
     */
    data class UserLoggedOut(
        val username: String,
        override val timestamp: Instant = Instant.now()
    ) : AuthenticationEvent(timestamp, "user.logged_out")
    
    /**
     * Event fired when a user registers
     */
    data class UserRegistered(
        val username: String,
        override val timestamp: Instant = Instant.now()
    ) : AuthenticationEvent(timestamp, "user.registered")
    
    /**
     * Event fired when a password reset is requested
     */
    data class PasswordResetRequested(
        val username: String,
        override val timestamp: Instant = Instant.now()
    ) : AuthenticationEvent(timestamp, "user.password_reset_requested")
    
    /**
     * Event fired when a password is reset
     */
    data class PasswordReset(
        val username: String,
        override val timestamp: Instant = Instant.now()
    ) : AuthenticationEvent(timestamp, "user.password_reset")
}
