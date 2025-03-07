package com.adsearch.infrastructure.repository

import com.adsearch.domain.model.PasswordResetToken
import com.adsearch.domain.port.PasswordResetTokenRepositoryPort
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory implementation of PasswordResetTokenRepositoryPort
 */
@Repository
class InMemoryPasswordResetTokenRepository : PasswordResetTokenRepositoryPort {
    
    private val tokens = ConcurrentHashMap<UUID, PasswordResetToken>()
    private val tokenIndex = ConcurrentHashMap<String, UUID>()
    private val userTokens = ConcurrentHashMap<UUID, MutableSet<UUID>>()
    
    override suspend fun findByToken(token: String): PasswordResetToken? {
        val tokenId = tokenIndex[token] ?: return null
        return tokens[tokenId]
    }
    
    override suspend fun findByUserId(userId: UUID): List<PasswordResetToken> {
        val tokenIds = userTokens[userId] ?: return emptyList()
        return tokenIds.mapNotNull { tokens[it] }
    }
    
    override suspend fun save(token: PasswordResetToken): PasswordResetToken {
        tokens[token.id] = token
        tokenIndex[token.token] = token.id
        
        userTokens.computeIfAbsent(token.userId) { mutableSetOf() }.add(token.id)
        
        return token
    }
    
    override suspend fun deleteById(id: UUID) {
        val token = tokens[id] ?: return
        
        tokens.remove(id)
        tokenIndex.remove(token.token)
        userTokens[token.userId]?.remove(id)
    }
    
    override suspend fun deleteByUserId(userId: UUID) {
        val tokenIds = userTokens[userId] ?: return
        
        tokenIds.forEach { tokenId ->
            val token = tokens[tokenId] ?: return@forEach
            tokens.remove(tokenId)
            tokenIndex.remove(token.token)
        }
        
        userTokens.remove(userId)
    }
}
