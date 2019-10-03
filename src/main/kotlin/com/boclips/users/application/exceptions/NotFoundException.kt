package com.boclips.users.application.exceptions

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

open class NotFoundException(message: String) : BoclipsApiException(
    ExceptionDetails(
        error = HttpStatus.NOT_FOUND.reasonPhrase,
        message = message,
        status = HttpStatus.NOT_FOUND
))