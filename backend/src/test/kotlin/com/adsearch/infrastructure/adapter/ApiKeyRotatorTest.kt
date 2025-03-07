package com.adsearch.infrastructure.adapter

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertFalse
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test

class ApiKeyRotatorTest {
    
    @Test
    fun `should return null when no API keys are configured`() {
        // Given
        val rotator = ApiKeyRotator(emptyList())
        
        // When
        val key = rotator.getNextApiKey()
        
        // Then
        assertNull(key)
        assertFalse(rotator.areAllKeysExhausted())
    }
    
    @Test
    fun `should rotate through available API keys`() {
        // Given
        val keys = listOf("key1", "key2", "key3")
        val rotator = ApiKeyRotator(keys)
        
        // When/Then
        assertEquals("key1", rotator.getNextApiKey())
        assertEquals("key2", rotator.getNextApiKey())
        assertEquals("key3", rotator.getNextApiKey())
        assertEquals("key1", rotator.getNextApiKey()) // Should cycle back to the first key
    }
    
    @Test
    fun `should skip exhausted API keys`() {
        // Given
        val keys = listOf("key1", "key2", "key3")
        val rotator = ApiKeyRotator(keys)
        
        // When
        rotator.markKeyExhausted("key1")
        rotator.markKeyExhausted("key3")
        
        // Then
        assertEquals("key2", rotator.getNextApiKey())
        assertEquals("key2", rotator.getNextApiKey()) // Should keep returning the only non-exhausted key
        assertFalse(rotator.areAllKeysExhausted())
    }
    
    @Test
    fun `should return null when all API keys are exhausted`() {
        // Given
        val keys = listOf("key1", "key2")
        val rotator = ApiKeyRotator(keys)
        
        // When
        rotator.markKeyExhausted("key1")
        rotator.markKeyExhausted("key2")
        
        // Then
        assertNull(rotator.getNextApiKey())
        assertTrue(rotator.areAllKeysExhausted())
    }
    
    @Test
    fun `should reset exhausted keys`() {
        // Given
        val keys = listOf("key1", "key2")
        val rotator = ApiKeyRotator(keys)
        
        // When
        rotator.markKeyExhausted("key1")
        rotator.markKeyExhausted("key2")
        assertTrue(rotator.areAllKeysExhausted())
        
        rotator.resetExhaustedKeys()
        
        // Then
        assertFalse(rotator.areAllKeysExhausted())
        assertEquals("key1", rotator.getNextApiKey())
    }
}
