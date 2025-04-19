package com.adsearch.common.exception.technical

import com.adsearch.common.enum.HttpStatusEnum
import com.adsearch.common.enum.TechnicalErrorCodeEnum
import com.adsearch.common.exception.BaseTechnicalException

class MailSendException(
    message: String = TechnicalErrorCodeEnum.MAIL_SEND_EXCEPTION.defaultMessage,
    errorCode: String = TechnicalErrorCodeEnum.MAIL_SEND_EXCEPTION.code,
    httpStatusEnum: HttpStatusEnum = HttpStatusEnum.INTERNAL_SERVER_ERROR,
    cause: Throwable? = null
) : BaseTechnicalException(message, errorCode, httpStatusEnum, cause) {
}
