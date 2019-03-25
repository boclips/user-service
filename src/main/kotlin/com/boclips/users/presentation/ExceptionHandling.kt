package com.boclips.users.presentation

import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.infrastructure.keycloak.UserAlreadyExistsException
import com.boclips.users.presentation.controllers.PresentationPackageMarker
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

    @ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "User permission denied")
    @ExceptionHandler(PermissionDeniedException::class)
    fun handleIOException(ex: PermissionDeniedException) {
        logger.error { "User has no permissions to access resource $ex" }
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "User not authenticated")
    @ExceptionHandler(NotAuthenticatedException::class)
    fun handleIOException(ex: NotAuthenticatedException) {
        logger.error { "Resource requires user to be authenticated $ex" }
    }
}
