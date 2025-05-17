package com.adsearch.integration.util

import com.icegreen.greenmail.spring.GreenMailBean
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component
import jakarta.mail.internet.MimeMessage

data class EmailMessage(
    val id: String,
    val subject: String,
    val text: String,
    val from: EmailAddress,
    val to: List<EmailAddress>
)

data class EmailAddress(
    val address: String
)

@Component
@Profile("test")
class GreenMailUtil(private val greenMailBean: GreenMailBean) {

    fun fetchLatestMail(): EmailMessage? {
        val messages = greenMailBean.receivedMessages
        if (messages.isEmpty()) {
            return null
        }
        
        val latestMessage = messages[messages.size - 1]
        return convertToEmailMessage(latestMessage)
    }
    
    private fun convertToEmailMessage(mimeMessage: MimeMessage): EmailMessage {
        val from = mimeMessage.from.first().toString()
        val toAddresses = mimeMessage.getRecipients(jakarta.mail.Message.RecipientType.TO)
            ?.map { EmailAddress(it.toString()) } ?: emptyList()
            
        // Extract text content properly handling multipart messages
        val text = getTextFromMessage(mimeMessage)
            
        return EmailMessage(
            id = mimeMessage.messageID ?: "",
            subject = mimeMessage.subject ?: "",
            text = text,
            from = EmailAddress(from),
            to = toAddresses
        )
    }
    
    private fun getTextFromMessage(message: MimeMessage): String {
        try {
            val content = message.content
            return extractTextFromContent(content)
        } catch (e: Exception) {
            e.printStackTrace()
            return "Error extracting message content: ${e.message}"
        }
    }
    
    private fun extractTextFromContent(content: Any?): String {
        if (content == null) return ""
        
        return when {
            content is String -> content
            content is jakarta.mail.Multipart -> {
                val sb = StringBuilder()
                
                for (i in 0 until content.count) {
                    val part = content.getBodyPart(i)
                    val partContent = part.content
                    
                    // Handle nested multipart content
                    if (partContent is jakarta.mail.Multipart) {
                        sb.append(extractTextFromContent(partContent))
                    } else if (part.disposition == null || part.disposition.equals(jakarta.mail.Part.INLINE, ignoreCase = true)) {
                        when {
                            part.contentType.lowercase().contains("text/plain") -> {
                                sb.append(partContent.toString())
                            }
                            part.contentType.lowercase().contains("text/html") -> {
                                sb.append(partContent.toString())
                            }
                        }
                    }
                }
                
                sb.toString()
            }
            else -> content.toString()
        }
    }
}
