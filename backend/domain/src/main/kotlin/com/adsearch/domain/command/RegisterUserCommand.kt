package com.adsearch.domain.command

data class RegisterUserCommand(
    val username: String,
    val email: String,
    var password: String,
)
