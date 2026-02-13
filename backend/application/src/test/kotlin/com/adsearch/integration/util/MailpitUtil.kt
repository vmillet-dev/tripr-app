package com.adsearch.integration.util

import com.adsearch.integration.BaseIT.Companion.MAILPIT_CONTAINER
import com.fasterxml.jackson.annotation.JsonProperty
import org.springframework.context.annotation.Profile
import org.springframework.core.ParameterizedTypeReference
import org.springframework.stereotype.Component
import org.springframework.test.web.servlet.client.RestTestClient

//inline fun <reified T> RestTestClient.ResponseSpec.body(): T? =
//    this.expectBody(T::class.java)
//        .returnResult()
//        .responseBody

data class MailpitMessage(
    @param:JsonProperty("ID")
    val id: String,
    @param:JsonProperty("Subject")
    val subject: String,
    @param:JsonProperty("Text")
    val text: String,
    @param:JsonProperty("From")
    val from: MailpitAddress,
    @param:JsonProperty("To")
    val to: List<MailpitAddress>,
)

data class MailpitAddress(
    @param:JsonProperty("Address")
    val address: String
)

@Component
@Profile("test")
class MailpitUtil {

    val restTestMailClient: RestTestClient = RestTestClient.bindToServer()
        .baseUrl("http://localhost:${MAILPIT_CONTAINER.getMappedPort(8025)}")
        .build()

    fun fetchLatestMail(): MailpitMessage? {
        return restTestMailClient
            .get()
            .uri("/api/v1/message/latest")
            .exchange()
            .expectBody(object : ParameterizedTypeReference<MailpitMessage>() {})
            .returnResult()
            .responseBody
    }
}
