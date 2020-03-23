package com.boclips.users.application.exceptions

import com.boclips.web.exceptions.BoclipsApiException
import com.boclips.web.exceptions.ExceptionDetails
import org.springframework.http.HttpStatus

open class AlreadyExistsException(message: String) : BoclipsApiException(
    ExceptionDetails(
        error = HttpStatus.CONFLICT.reasonPhrase,
        message = message,
        status = HttpStatus.CONFLICT
    )
)
