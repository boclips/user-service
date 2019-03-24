package com.boclips.users.presentation

import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.infrastructure.keycloak.UserAlreadyExistsException
import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice(basePackageClasses = [PresentationPackageMarker::class])
class ExceptionHandling {
    companion object : KLogging()

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "User already exists")
    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleIOException(ex: UserAlreadyExistsException) {
        logger.error { "User already exists $ex" }
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "User already exists")
    @ExceptionHandler(PermissionDeniedException::class)
    fun handleIOException(ex: PermissionDeniedException) {
        logger.error { "User has no permissions to access resource $ex" }
    }
}
