package com.adsearch.domain.port.out.persistence

import com.adsearch.domain.model.User

/**
 * Port for user repository operations
 */
interface UserPersistencePort {

    /**
     * Find a user by username
     */
    fun findByUsername(username: String): User?

    fun findById(id: Long): User?

    /**
     * Find a user by email
     */
    fun findByEmail(email: String): User?

    /**
     * Save a user
     */
    fun save(user: User)
}
