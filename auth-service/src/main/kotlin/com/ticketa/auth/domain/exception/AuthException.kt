package com.ticketa.auth.domain.exception

abstract class AuthException(message: String) : RuntimeException(message)

class EmailAlreadyExistsException(email: String) :
    AuthException("User with email $email already exists")

class InvalidCredentialsException :
    AuthException("Invalid email or password")

class InvalidRefreshTokenException :
    AuthException("Invalid or expired refresh token")

class TokenRevokedException :
    AuthException("Token has been revoked")
