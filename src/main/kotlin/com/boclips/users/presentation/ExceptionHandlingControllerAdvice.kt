package com.boclips.users.presentation

import com.boclips.users.application.exceptions.CaptchaScoreBelowThresholdException
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.domain.model.UserNotFoundException
import com.boclips.users.infrastructure.keycloak.UserAlreadyExistsException
import com.boclips.users.presentation.controllers.PresentationPackageMarker
import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus

@ControllerAdvice(basePackageClasses = [PresentationPackageMarker::class])
class ExceptionHandlingControllerAdvice {
    companion object : KLogging()

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "User already exists")
    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleIOException(ex: UserAlreadyExistsException) {
        logger.info { "User already exists $ex" }
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "User permission denied")
    @ExceptionHandler(PermissionDeniedException::class)
    fun handleIOException(ex: PermissionDeniedException) {
        logger.info { "User has no permissions to access resource $ex" }
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "User not authenticated")
    @ExceptionHandler(NotAuthenticatedException::class)
    fun handleIOException(ex: NotAuthenticatedException) {
        logger.info { "Resource requires user to be authenticated $ex" }
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "User failed Captcha challenge")
    @ExceptionHandler(CaptchaScoreBelowThresholdException::class)
    fun handleIOException(ex: CaptchaScoreBelowThresholdException) {
        logger.info { "It is assumed ${ex.identifier} is a robot" }
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "User not found")
    @ExceptionHandler(UserNotFoundException::class)
    fun handleIOException(ex: UserNotFoundException) {
        logger.info { "User ${ex.userId} not found" }
    }
}
