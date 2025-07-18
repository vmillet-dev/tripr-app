package com.adsearch.domain.model.enum

enum class TechnicalErrorCodeEnum(val code: String, val defaultMessage: String) {
    MAIL_SEND_EXCEPTION("TECH_001", "Error encountered while sending mail."),
}
