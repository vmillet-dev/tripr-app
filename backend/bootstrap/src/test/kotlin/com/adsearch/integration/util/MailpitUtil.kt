package com.adsearch.integration.util

import com.adsearch.integration.BaseIT.Companion.MAILPIT_CONTAINER
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

data class MailpitMessage(
    @JsonProperty("ID")
    val id: String,
    @JsonProperty("Subject")
    val subject: String,
    @JsonProperty("Text")
    val text: String,
    @JsonProperty("From")
    val from: MailpitAddress,
    @JsonProperty("To")
    val to: List<MailpitAddress>,
)

data class MailpitAddress(
    @JsonProperty("Address")
    val address: String
)

@Component
@Profile("test")
class MailpitUtil(private val restTemplate: TestRestTemplate) {

    fun fetchLatestMail(): MailpitMessage? {
        val mailpitResponse = restTemplate.getForEntity(
            "http://localhost:${MAILPIT_CONTAINER.getMappedPort(8025)}/api/v1/message/latest",
            MailpitMessage::class.java
        )
        return mailpitResponse.body
    }
}
