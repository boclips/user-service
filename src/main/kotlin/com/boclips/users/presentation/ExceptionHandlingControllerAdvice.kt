package com.boclips.users.presentation

import com.boclips.users.application.exceptions.AlreadyExistsException
import com.boclips.users.application.exceptions.CaptchaScoreBelowThresholdException
import com.boclips.users.application.exceptions.ContractExistsException
import com.boclips.users.application.exceptions.NotAuthenticatedException
import com.boclips.users.application.exceptions.NotFoundException
import com.boclips.users.application.exceptions.PermissionDeniedException
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

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Already exists")
    @ExceptionHandler(AlreadyExistsException::class)
    fun handleAlreadyExists(ex: AlreadyExistsException) {
        logger.info { ex.message }
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

    @ResponseStatus(value = HttpStatus.NOT_FOUND, reason = "Not found")
    @ExceptionHandler(NotFoundException::class)
    fun handleNotFound(ex: NotFoundException) {
        logger.info { ex.message }
    }

    @ResponseStatus(value = HttpStatus.CONFLICT, reason = "Contract already exists for given name")
    @ExceptionHandler(ContractExistsException::class)
    fun handleContractExists(ex: ContractExistsException) {
        logger.info { ex.message }
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST, reason = "Validation failed")
    @ExceptionHandler(ConstraintViolationException::class)
    fun handleConstraintViolationException(ex: ConstraintViolationException) {
        logger.info { ex.message }
    }
}
