package com.adsearch.domain.model.command

data class RegisterUserCommand(
    val username: String,
    val email: String,
    var password: String,
)
