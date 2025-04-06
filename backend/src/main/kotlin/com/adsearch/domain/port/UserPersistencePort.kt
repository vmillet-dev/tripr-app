package com.adsearch.domain.port

import com.adsearch.domain.model.User

/**
 * Port for user repository operations
 */
interface UserPersistencePort {

    /**
     * Find a user by username
     */
    fun findByUsername(username: String): User?

    /**
     * Find a user by ID
     */
    suspend fun findById(id: Long): User?

    /**
     * Save a user
     */
    suspend fun save(user: User): User

    /**
     * Find all users
     */
    suspend fun findAll(): List<User>
}
