package com.adsearch.domain.enum

enum class FunctionalErrorCodeEnum(val code: String, val defaultMessage: String) {
    INVALID_CREDENTIALS("FUNC_001", "Invalid username or password"),
    TOKEN_EXPIRED("FUNC_002", "Authentication token has expired"),
    INVALID_TOKEN("FUNC_003", "Invalid authentication token"),
    USERNAME_ALREADY_EXISTS("FUNC_004", "The username already exists."),
    USER_NOT_FOUND("FUNC_005", "The user was not found."),
    EMAIL_ALREADY_EXISTS("FUNC_006", "The email already exists."),
}
