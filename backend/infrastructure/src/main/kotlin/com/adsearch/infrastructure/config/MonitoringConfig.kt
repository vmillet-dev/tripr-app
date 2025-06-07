package com.adsearch.infrastructure.config

import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.core.instrument.Timer
import org.springframework.boot.actuate.autoconfigure.metrics.MeterRegistryCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.stereotype.Component

@Configuration
class MonitoringConfig {

    @Bean
    fun metricsCommonTags(): MeterRegistryCustomizer<MeterRegistry> {
        return MeterRegistryCustomizer { registry ->
            registry.config().commonTags("application", "tripr-app")
        }
    }
}

@Component
class CustomMetrics(private val meterRegistry: MeterRegistry) {

    fun recordUserLogin(username: String) {
        meterRegistry.counter("user.login.attempts", "username", username).increment()
    }

    fun recordUserLoginFailure(username: String) {
        meterRegistry.counter("user.login.failures", "username", username).increment()
    }

    fun recordApiCall(endpoint: String, method: String, status: String): Timer.Sample {
        return Timer.start(meterRegistry)
    }

    fun recordApiCallEnd(sample: Timer.Sample, endpoint: String, method: String, status: String) {
        sample.stop(Timer.builder("api.request.duration")
            .tag("endpoint", endpoint)
            .tag("method", method)
            .tag("status", status)
            .register(meterRegistry))
    }

    fun recordPasswordResetRequest() {
        meterRegistry.counter("password.reset.requests").increment()
    }

    fun recordPasswordResetSuccess() {
        meterRegistry.counter("password.reset.success").increment()
    }
}
