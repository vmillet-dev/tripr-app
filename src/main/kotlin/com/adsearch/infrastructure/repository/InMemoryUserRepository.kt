package com.adsearch.infrastructure.repository

import com.adsearch.domain.model.User
import com.adsearch.domain.port.UserRepositoryPort
import kotlinx.coroutines.runBlocking
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory implementation of UserRepositoryPort
 */
@Repository
class InMemoryUserRepository(
    private val passwordEncoder: PasswordEncoder
) : UserRepositoryPort {
    
    private val users = ConcurrentHashMap<UUID, User>()
    private val usernameIndex = ConcurrentHashMap<String, UUID>()
    
    init {
        // Add a default user for testing with a fixed ID for consistency
        val defaultUser = User(
            id = UUID.fromString("11111111-1111-1111-1111-111111111111"),
            username = "user",
            password = passwordEncoder.encode("password"),
            roles = mutableListOf("USER")
        )
        
        // Use runBlocking to call suspend functions from non-suspend context
        runBlocking {
            save(defaultUser)
            
            // Add an admin user for testing with a fixed ID for consistency
            val adminUser = User(
                id = UUID.fromString("22222222-2222-2222-2222-222222222222"),
                username = "admin",
                password = passwordEncoder.encode("admin"),
                roles = mutableListOf("USER", "ADMIN")
            )
            
            save(adminUser)
        }
    }
    
    override suspend fun findByUsername(username: String): User? {
        val userId = usernameIndex[username] ?: return null
        return users[userId]
    }
    
    override suspend fun findById(id: UUID): User? {
        return users[id]
    }
    
    override suspend fun save(user: User): User {
        users[user.id] = user
        usernameIndex[user.username] = user.id
        return user
    }
    
    override suspend fun findAll(): List<User> {
        return users.values.toList()
    }
}
