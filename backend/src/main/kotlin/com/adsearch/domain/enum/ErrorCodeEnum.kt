package com.adsearch.domain.enum

enum class ErrorCodeEnum(val code: String, val defaultMessage: String,) {
    // Functional exceptions
    GENERIC_FUNCTIONAL("FUNC_000", "A functional error has occurred."),
    INVALID_CREDENTIALS("FUNC_001", "Invalid username or password"),
    TOKEN_EXPIRED("FUNC_002", "Authentication token has expired"),
    INVALID_TOKEN("FUNC_003", "Invalid authentication token"),
    USER_ALREADY_EXISTS("FUNC_004", "The username already exists."),
    USER_NOT_FOUND("FUNC_005", "The user was not found."),

    // Technical exceptions
}
