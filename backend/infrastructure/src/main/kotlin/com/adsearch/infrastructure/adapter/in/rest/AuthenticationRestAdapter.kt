package com.adsearch.infrastructure.adapter.`in`.rest

import com.adsearch.domain.port.`in`.*
import com.adsearch.infrastructure.adapter.`in`.rest.dto.*
import com.adsearch.infrastructure.adapter.`in`.rest.utils.ServletRequestUtils.currentRequest
import com.adsearch.infrastructure.adapter.`in`.rest.utils.ServletRequestUtils.currentResponse
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.Cookie
import jakarta.validation.Valid
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Suppress("SpringJavaInjectionPointsAutowiringInspection")
@RestController
@Tag(name = "Authentication", description = "API for user authentication and token management")
@PreAuthorize("permitAll()")
class AuthenticationRestAdapter(
    private val loginUserUseCase: LoginUserUseCase,
    private val createUserUseCase: CreateUserUseCase,
    private val logoutUserUseCase: LogoutUserUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
    private val passwordResetUseCase: PasswordResetUseCase,
    @param:Value($$"${jwt.refresh-token.cookie-name}") private val cookieName: String,
    @param:Value($$"${jwt.refresh-token.expiration}") private val cookieMaxAge: Int
) : AuthenticationApi {

    companion object {
        val LOG: Logger = LoggerFactory.getLogger(this::class.java)
    }

    override fun login(@Valid @RequestBody authRequestDto: AuthRequestDto): ResponseEntity<AuthResponseDto> {
        val authResponse = loginUserUseCase.login(LoginUserUseCase.LoginUserCommand(authRequestDto.username, authRequestDto.password))

        authResponse.refreshToken.let { token ->
            buildCookie(
                cookieName,
                token,
                cookieMaxAge
            ).also { currentResponse().addCookie(it) }
        }
        return ResponseEntity.ok(AuthResponseDto(authResponse.accessToken))
    }


    override fun register(@Valid @RequestBody registerRequestDto: RegisterRequestDto): ResponseEntity<Register200Response> {
        createUserUseCase.createUser(
            CreateUserUseCase.RegisterUserCommand(
                registerRequestDto.username,
                registerRequestDto.email,
                registerRequestDto.password
            )
        )

        return ResponseEntity.ok(Register200Response("UserEntity registered successfully"))
    }

    override fun refreshToken(): ResponseEntity<AuthResponseDto> {
        val cookies: String? = currentRequest().cookies?.find { it.name == cookieName }?.value
        val authResponse = refreshTokenUseCase.refreshAccessToken(cookies)

        return ResponseEntity.ok(AuthResponseDto(authResponse.accessToken))
    }

    override fun logout(): ResponseEntity<Logout200Response> {
        val cookies: String? = currentRequest().cookies?.find { it.name == cookieName }?.value

        logoutUserUseCase.logout(cookies)
        buildCookie(cookieName, "", 0).also { currentResponse().addCookie(it) }
        return ResponseEntity.ok(Logout200Response("Logged out successfully"))
    }

    override fun requestPasswordReset(@Valid @RequestBody passwordResetRequestDto: PasswordResetRequestDto): ResponseEntity<RequestPasswordReset200Response> {
        LOG.info("Received password reset request for username: ${passwordResetRequestDto.username}")
        passwordResetUseCase.requestPasswordReset(passwordResetRequestDto.username)
        return ResponseEntity.ok(RequestPasswordReset200Response("If the username exists, a password reset email has been sent"))
    }

    override fun resetPassword(@Valid @RequestBody passwordResetDto: PasswordResetDto): ResponseEntity<ResetPassword200Response> {
        LOG.info("Received password reset with token")
        passwordResetUseCase.resetPassword(passwordResetDto.token, passwordResetDto.newPassword)
        return ResponseEntity.ok(ResetPassword200Response("Password has been reset successfully"))
    }

    override fun validateToken(@RequestParam token: String): ResponseEntity<ValidateToken200Response> {
        LOG.info("Validating password reset token")
        val isValid = passwordResetUseCase.validateToken(token)
        return ResponseEntity.ok(ValidateToken200Response(isValid))
    }

    private fun buildCookie(name: String, value: String, maxAge: Int): Cookie =
        Cookie(name, value).apply {
            this.maxAge = maxAge
            path = "/"
            isHttpOnly = true
            // TODO: enable in prod → secure = true
        }
}
