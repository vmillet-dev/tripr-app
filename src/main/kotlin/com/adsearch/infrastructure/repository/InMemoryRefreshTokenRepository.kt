package com.adsearch.infrastructure.repository

import com.adsearch.domain.model.RefreshToken
import com.adsearch.domain.port.RefreshTokenRepositoryPort
import org.springframework.stereotype.Repository
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * In-memory implementation of RefreshTokenRepositoryPort
 */
@Repository
class InMemoryRefreshTokenRepository : RefreshTokenRepositoryPort {
    
    private val refreshTokens = ConcurrentHashMap<UUID, RefreshToken>()
    private val tokenIndex = ConcurrentHashMap<String, UUID>()
    private val userTokensIndex = ConcurrentHashMap<UUID, MutableSet<UUID>>()
    
    override suspend fun findByToken(token: String): RefreshToken? {
        val tokenId = tokenIndex[token] ?: return null
        return refreshTokens[tokenId]
    }
    
    override suspend fun findByUserId(userId: UUID): List<RefreshToken> {
        val tokenIds = userTokensIndex[userId] ?: return emptyList()
        return tokenIds.mapNotNull { refreshTokens[it] }
    }
    
    override suspend fun save(refreshToken: RefreshToken): RefreshToken {
        refreshTokens[refreshToken.id] = refreshToken
        tokenIndex[refreshToken.token] = refreshToken.id
        
        userTokensIndex.computeIfAbsent(refreshToken.userId) { mutableSetOf() }
            .add(refreshToken.id)
        
        return refreshToken
    }
    
    override suspend fun deleteById(id: UUID) {
        val token = refreshTokens[id] ?: return
        
        refreshTokens.remove(id)
        tokenIndex.remove(token.token)
        userTokensIndex[token.userId]?.remove(id)
    }
    
    override suspend fun deleteByUserId(userId: UUID) {
        val tokenIds = userTokensIndex[userId] ?: return
        
        tokenIds.forEach { tokenId ->
            val token = refreshTokens[tokenId]
            if (token != null) {
                tokenIndex.remove(token.token)
                refreshTokens.remove(tokenId)
            }
        }
        
        userTokensIndex.remove(userId)
    }
}
