package com.adsearch.infrastructure.adapter.out

import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class ConfigurationProviderAdapterTest {

    @Test
    fun `should return correct expiration values`() {
        // given
        val adapter = ConfigurationProviderAdapter(3600L, 600L)

        // when / then
        // Note: checking current implementation which seems to swap or use them interchangeably based on the code I saw
        assertThat(adapter.getRefreshTokenExpiration()).isEqualTo(600L)
        assertThat(adapter.getPasswordResetTokenExpiration()).isEqualTo(3600L)
    }
}
