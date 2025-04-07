package com.adsearch.integration

import com.adsearch.common.mapper.DynamicMapper
import com.adsearch.common.mapper.mapTo
import com.adsearch.common.mapper.mapToList
import com.adsearch.infrastructure.adapter.out.persistence.mapper.PasswordResetTokenEntityMapper
import com.adsearch.infrastructure.adapter.out.persistence.mapper.RefreshTokenEntityMapper
import com.adsearch.infrastructure.adapter.out.persistence.mapper.UserEntityMapper
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan

@TestConfiguration
@ComponentScan(basePackages = ["com.adsearch.common.mapper"])
class TestConfig {
    
    @Bean
    fun userEntityMapper(): UserEntityMapper {
        return UserEntityMapper()
    }
    
    @Bean
    fun refreshTokenEntityMapper(): RefreshTokenEntityMapper {
        return RefreshTokenEntityMapper()
    }
    
    @Bean
    fun passwordResetTokenEntityMapper(): PasswordResetTokenEntityMapper {
        return PasswordResetTokenEntityMapper()
    }
}
