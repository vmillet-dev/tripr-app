package com.adsearch.integration.config

import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory
import org.springframework.web.client.RestTemplate


@TestConfiguration
// Needed because of the lack of output steam mode handling by spring implementation of request factory, using apache:httpclient5
class RestTemplateConfig {
    @Bean
    fun restTemplate(): RestTemplate {
        val template = RestTemplate()
        template.requestFactory = HttpComponentsClientHttpRequestFactory()
        return template
    }
}
