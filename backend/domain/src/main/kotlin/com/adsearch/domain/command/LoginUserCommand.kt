package com.adsearch.domain.command

data class LoginUserCommand(
    val username: String,
    val password: String
)
