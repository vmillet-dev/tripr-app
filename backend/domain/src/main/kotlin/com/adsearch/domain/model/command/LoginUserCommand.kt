package com.adsearch.domain.model.command

data class LoginUserCommand(
    val username: String,
    val password: String
)
