package com.adsearch.domain.port.out

import com.adsearch.domain.model.UserDom

/**
 * Port for user repository operations
 */
interface UserPersistencePort {

    /**
     * Find a user by username
     */
    fun findByUsername(username: String): UserDom?

    /**
     * Find a user by email
     */
    fun findByEmail(email: String): UserDom?

    /**
     * Save a user
     */
    fun save(userDom: UserDom)
}
