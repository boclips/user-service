package com.boclips.users.presentation

import com.boclips.users.application.exceptions.CaptchaScoreBelowThresholdException
import com.boclips.users.application.exceptions.ContractNotFoundException
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.application.exceptions.PermissionDeniedException
import com.boclips.users.application.exceptions.UserNotFoundException
import com.boclips.users.infrastructure.keycloak.UserAlreadyExistsException
import com.boclips.users.presentation.controllers.PresentationPackageMarker
import mu.KLogging
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.ResponseStatus
import javax.validation.ConstraintViolationException

@ControllerAdvice(basePackageClasses = [PresentationPackageMarker::class])
class ExceptionHandlingControllerAdvice {
    companion object : KLogging()

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "User already exists")
    @ExceptionHandler(UserAlreadyExistsException::class)
    fun handleUserAlreadyExists(ex: UserAlreadyExistsException) {
        logger.info { "User already exists $ex" }
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN, reason = "User permission denied")
    @ExceptionHandler(PermissionDeniedException::class)
    fun handlePermissionDenied(ex: PermissionDeniedException) {
        logger.info { "User has no permissions to access resource $ex" }
    }

    @ResponseStatus(value = HttpStatus.UNAUTHORIZED, reason = "User not authenticated")
    @ExceptionHandler(NotAuthenticatedException::class)
    fun handleNotAuthenticated(ex: NotAuthenticatedException) {
        logger.info { "Resource requires user to be authenticated $ex" }
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "User failed Captcha challenge")
    @ExceptionHandler(CaptchaScoreBelowThresholdException::class)
    fun handleCaptchaScoreException(ex: CaptchaScoreBelowThresholdException) {
        logger.info { "It is assumed ${ex.identifier} is a robot" }
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "User not found")
    @ExceptionHandler(UserNotFoundException::class)
    fun handleUserNotFound(ex: UserNotFoundException) {
        logger.info { "User ${ex.userId} not found" }
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Contract not found")
    @ExceptionHandler(ContractNotFoundException::class)
    fun handleContractNotFound(ex: ContractNotFoundException) {
        logger.info { "Contract for ${ex.criteria} not found" }
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Validation failed")
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException) {
        logger.info { ex.message }
    }
}
